package no.nav.fo.veilarbregistrering.bruker.pdl;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.httpclient.BaseClient;
import no.nav.log.MDCConstants;
import no.nav.sbl.rest.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Entity;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

class PdlOppslagClient extends BaseClient {

    private static final Logger LOG = LoggerFactory.getLogger(PdlOppslagClient.class);

    private final String NAV_CONSUMER_TOKEN_HEADER = "Nav-Consumer-Token";
    private final String NAV_PERSONIDENT_HEADER = "Nav-Personident";
    private final String NAV_CALL_ID_HEADER = "Nav-Call-Id";
    private final String TEMA_HEADER = "Tema";
    private final String OPPFOLGING_TEMA_HEADERVERDI = "OPP";

    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateDeserializer()).create();

    private OidcSystemUserTokenProvider oidcSystemUserTokenProvider;

    PdlOppslagClient(
            String baseUrl,
            Provider<HttpServletRequest> httpServletRequestProvider,
            OidcSystemUserTokenProvider oidcSystemUserTokenProvider) {
        super(baseUrl, httpServletRequestProvider);
        this.oidcSystemUserTokenProvider = oidcSystemUserTokenProvider;
    }

    Optional<PdlPerson> hentPerson(AktorId aktorId) {
        PdlRequest request = new PdlRequest(hentQuery(), new Variables(aktorId.asString(), false));
        String json = pdlJson(aktorId.asString(), request);
        LOG.info("json-response fra PDL: {}", json);
        PdlResponse resp = gson.fromJson(json, PdlResponse.class);
        validateResponse(resp);
        return Optional.of(resp.getData().getHentPerson());
    }

    String pdlJson(String fnr, PdlRequest request) {
        String token = this.oidcSystemUserTokenProvider.getSystemUserAccessToken();

        return RestUtils.withClient(client ->
                client.target(baseUrl)
                        .request()
                        .header(NAV_PERSONIDENT_HEADER, fnr)
                        .header(NAV_CALL_ID_HEADER, MDC.get(MDCConstants.MDC_CALL_ID))
                        .header("Authorization", "Bearer " + token)
                        .header(NAV_CONSUMER_TOKEN_HEADER, "Bearer " + token)
                        .header(TEMA_HEADER, OPPFOLGING_TEMA_HEADERVERDI)
                        .post(Entity.json(request), String.class));
    }

    private void validateResponse(PdlResponse response) {
        if (response.getErrors() != null && response.getErrors().size() > 0) {
            LOG.warn("Error from PDL: " + gson.toJson(response.getErrors()));
            throw new RuntimeException("Error from PDL");
        }
    }

    private String hentQuery() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(PdlOppslagClient.class.getResource("/pdl/hentPerson.graphql").toURI()));
            return new String(bytes).replaceAll("[\n\r]]", "");
        } catch (IOException e) {
           throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {

            return Optional.ofNullable(json.getAsJsonPrimitive().getAsString())
                    .map(LocalDate::parse)
                    .orElse(null);
        }
    }
}
