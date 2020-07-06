package no.nav.fo.veilarbregistrering.bruker.pdl;

import com.google.gson.*;
import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.BrukerIkkeFunnetException;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.HentPersonVariables;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlHentPersonRequest;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlHentPersonResponse;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlPerson;
import no.nav.log.MDCConstants;
import no.nav.sbl.rest.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ws.rs.client.Entity;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

class PdlOppslagClient {

    private static final Logger LOG = LoggerFactory.getLogger(PdlOppslagClient.class);

    private final String NAV_CONSUMER_TOKEN_HEADER = "Nav-Consumer-Token";
    private final String NAV_PERSONIDENT_HEADER = "Nav-Personident";
    private final String NAV_CALL_ID_HEADER = "Nav-Call-Id";
    private final String TEMA_HEADER = "Tema";
    private final String OPPFOLGING_TEMA_HEADERVERDI = "OPP";

    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateDeserializer()).create();

    private final String baseUrl;

    private SystemUserTokenProvider systemUserTokenProvider;

    PdlOppslagClient(
            String baseUrl,
            SystemUserTokenProvider systemUserTokenProvider) {
        this.baseUrl = baseUrl;
        this.systemUserTokenProvider = systemUserTokenProvider;
    }

    PdlPerson hentPerson(AktorId aktorId) {
        PdlHentPersonRequest request = new PdlHentPersonRequest(hentPersonQuery(), new HentPersonVariables(aktorId.asString(), false));
        String json = hentPersonRequest(aktorId.asString(), request);
        LOG.debug("json-response fra PDL: {}", json);
        PdlHentPersonResponse resp = gson.fromJson(json, PdlHentPersonResponse.class);
        validateResponse(resp);
        return resp.getData().getHentPerson();
    }

    String hentPersonRequest(String fnr, PdlHentPersonRequest request) {
        String token = this.systemUserTokenProvider.getSystemUserAccessToken();

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

    private void validateResponse(PdlHentPersonResponse response) {
        if (response.getErrors() != null && response.getErrors().size() > 0) {
            if (response.getErrors().stream().anyMatch(PdlOppslagClient::not_found)) {
                throw new BrukerIkkeFunnetException("Fant ikke person i PDL");
            }

            throw new RuntimeException("Integrasjon mot PDL feilet: " + gson.toJson(response.getErrors()));
        }
    }

    private static boolean not_found(PdlError pdlError) {
        if (pdlError == null || pdlError.getExtensions() == null) {
            return false;
        }
        return "not_found".equals(pdlError.getExtensions().getCode());
    }

    private String hentPersonQuery() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(PdlOppslagClient.class.getResource("/pdl/hentPerson.graphql").toURI()));
            return new String(bytes).replaceAll("[\n\r]]", "");
        } catch (IOException | URISyntaxException e) {
           throw new RuntimeException("Integrasjon mot PDL ble ikke gjennomført pga. feil ved lesing av query", e);
        }
    }

    private static class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {

            return Optional.ofNullable(json.getAsJsonPrimitive().getAsString())
                    .map(LocalDate::parse)
                    .orElse(null);
        }
    }
}
