package no.nav.fo.veilarbregistrering.bruker.pdl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway;
import no.nav.fo.veilarbregistrering.httpclient.BaseClient;
import no.nav.log.MDCConstants;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import no.nav.sbl.rest.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Entity;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

public class PdlOppslagGatewayImpl extends BaseClient implements PdlOppslagGateway {

    private final Logger logger = LoggerFactory.getLogger(PdlOppslagGatewayImpl.class);

    private final String NAV_CONSUMER_TOKEN_HEADER = "Nav-Consumer-Token";
    private final String NAV_PERSONIDENT_HEADER = "Nav-Personident";
    private final String NAV_CALL_ID_HEADER = "Nav-Call-Id";
    private final String TEMA_HEADER = "Tema";
    private final String ALLE_TEMA_HEADERVERDI = "GEN";

    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();

    private SystemUserTokenProvider systemUserTokenProvider;
    private final UnleashService unleashService;

    public PdlOppslagGatewayImpl(
            String baseUrl,
            Provider<HttpServletRequest> httpServletRequestProvider,
            UnleashService unleashService
    ) {
        super(baseUrl, httpServletRequestProvider);
        this.unleashService = unleashService;
    }

    public Optional<PdlPerson> hentPerson(String fnr) {
        if (!isPdlEnabled()) {
            return Optional.empty();
        }

        PdlRequest request = new PdlRequest(hentQuery(), new Variables(fnr, false));
        String json = pdlJson(fnr, request);
        PdlResponse resp = gson.fromJson(json, PdlResponse.class);
        validateResponse(resp);
        return Optional.of(resp.getData().getHentPerson());
    }

    String pdlJson(String fnr, PdlRequest request) {
        String token = (this.systemUserTokenProvider == null ? new SystemUserTokenProvider() : this.systemUserTokenProvider)
                .getToken();
        return RestUtils.withClient(client ->
                client.target(baseUrl)
                        .request()
                        .header(NAV_PERSONIDENT_HEADER, fnr)
                        .header(NAV_CALL_ID_HEADER, MDC.get(MDCConstants.MDC_CALL_ID))
                        .header("Authorization", token)
                        .header(NAV_CONSUMER_TOKEN_HEADER, token)
                        .header(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
                        .post(Entity.json(request), String.class));
    }

    private void validateResponse(PdlResponse response) {
        if (response.getErrors() != null && response.getErrors().size() > 0) {
            logger.warn("Error from PDL: " + gson.toJson(response.getErrors()));
            throw new RuntimeException("Error from PDL");
        }
    }

    private String hentQuery() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(PdlOppslagGatewayImpl.class.getResource("/pdl/hentPerson.graphql").toURI()));
            return new String(bytes).replaceAll("[\n\r]]", "");
        } catch (IOException e) {
           throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class LocalDateAdapter extends TypeAdapter<LocalDate> {
        @Override
        public void write(final JsonWriter jsonWriter, final LocalDate localDate ) throws IOException {
            jsonWriter.value(localDate.toString());
        }

        @Override
        public LocalDate read( final JsonReader jsonReader ) throws IOException {
            return LocalDate.parse(jsonReader.nextString());
        }
    }

    private boolean isPdlEnabled() {
        return unleashService.isEnabled("veilarbregistrering.pdlEnabled");
    }
}
