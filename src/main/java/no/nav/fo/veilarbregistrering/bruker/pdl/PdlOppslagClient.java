package no.nav.fo.veilarbregistrering.bruker.pdl;

import com.google.gson.*;
import no.nav.common.sts.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.feil.BrukerIkkeFunnetException;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentGeografiskTilknytning.HentGeografiskTilknytningVariables;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentGeografiskTilknytning.PdlGeografiskTilknytning;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentGeografiskTilknytning.PdlHentGeografiskTilknytningRequest;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentGeografiskTilknytning.PdlHentGeografiskTilknytningResponse;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.HentIdenterVariables;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlHentIdenterRequest;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlHentIdenterResponse;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdenter;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.HentPersonVariables;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlHentPersonRequest;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlHentPersonResponse;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlPerson;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Optional;

import static no.nav.common.rest.client.RestClient.baseClient;
import static no.nav.common.rest.client.RestUtils.getBodyStr;
import static no.nav.common.rest.client.RestUtils.toJsonRequestBody;
import static no.nav.common.utils.UrlUtils.joinPaths;

public class PdlOppslagClient {
    private final String NAV_CONSUMER_TOKEN_HEADER = "Nav-Consumer-Token";
    private final String NAV_PERSONIDENT_HEADER = "Nav-Personident";
    private final String TEMA_HEADER = "Tema";
    private final String OPPFOLGING_TEMA_HEADERVERDI = "OPP";

    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateDeserializer()).create();

    private final String baseUrl;

    private final SystemUserTokenProvider systemUserTokenProvider;

    PdlOppslagClient(
            String baseUrl,
            SystemUserTokenProvider systemUserTokenProvider) {
        this.baseUrl = baseUrl;
        this.systemUserTokenProvider = systemUserTokenProvider;
    }

    PdlIdenter hentIdenter(Foedselsnummer fnr) {
        PdlHentIdenterRequest request = new PdlHentIdenterRequest(hentIdenterQuery(), new HentIdenterVariables(fnr.stringValue()));
        String json = hentIdenterRequest(fnr.stringValue(), request);
        PdlHentIdenterResponse response = gson.fromJson(json, PdlHentIdenterResponse.class);
        validateResponse(response);
        return response.getData().getHentIdenter();
    }

    PdlIdenter hentIdenter(AktorId aktorId) {
        PdlHentIdenterRequest request = new PdlHentIdenterRequest(hentIdenterQuery(), new HentIdenterVariables(aktorId.asString()));
        String json = hentIdenterRequest(aktorId.asString(), request);
        PdlHentIdenterResponse response = gson.fromJson(json, PdlHentIdenterResponse.class);
        validateResponse(response);
        return response.getData().getHentIdenter();
    }

    String hentIdenterRequest(String personident, PdlHentIdenterRequest requestBody) {
        String token = this.systemUserTokenProvider.getSystemUserToken();

        Request request = new Request.Builder()
                .url(joinPaths(baseUrl, "/graphql"))
                .header(NAV_PERSONIDENT_HEADER, personident)
                .header("Authorization", "Bearer " + token)
                .header(NAV_CONSUMER_TOKEN_HEADER, "Bearer " + token)
                .method("POST", toJsonRequestBody(requestBody))
                .build();

        try (Response response = baseClient().newCall(request).execute()) {
            return getBodyStr(response).orElseThrow(RuntimeException::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    PdlGeografiskTilknytning hentGeografiskTilknytning(AktorId aktorId) {
        PdlHentGeografiskTilknytningRequest request = new PdlHentGeografiskTilknytningRequest(hentGeografisktilknytningQuery(), new HentGeografiskTilknytningVariables(aktorId.asString()));
        String json = hentGeografiskTilknytningRequest(aktorId.asString(), request);
        PdlHentGeografiskTilknytningResponse resp = gson.fromJson(json, PdlHentGeografiskTilknytningResponse.class);
        validateResponse(resp);
        return resp.getData().getHentGeografiskTilknytning();
    }

    String hentGeografiskTilknytningRequest(String fnr, PdlHentGeografiskTilknytningRequest pdlHentGeografiskTilknytningRequest) {
        String token = this.systemUserTokenProvider.getSystemUserToken();

        Request request = new Request.Builder()
                .url(joinPaths(baseUrl, "/graphql"))
                .header(NAV_PERSONIDENT_HEADER, fnr)
                .header("Authorization", "Bearer " + token)
                .header(NAV_CONSUMER_TOKEN_HEADER, "Bearer " + token)
                .header(TEMA_HEADER, OPPFOLGING_TEMA_HEADERVERDI)
                .method("POST", toJsonRequestBody(pdlHentGeografiskTilknytningRequest))
                .build();
        try (Response response = baseClient().newCall(request).execute()) {
            return getBodyStr(response).orElseThrow(RuntimeException::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    PdlPerson hentPerson(AktorId aktorId) {
        PdlHentPersonRequest request = new PdlHentPersonRequest(hentPersonQuery(), new HentPersonVariables(aktorId.asString(), false));
        String json = hentPersonRequest(aktorId.asString(), request);
        PdlHentPersonResponse resp = gson.fromJson(json, PdlHentPersonResponse.class);
        validateResponse(resp);
        return resp.getData().getHentPerson();
    }

    String hentPersonRequest(String fnr, PdlHentPersonRequest pdlHentPersonRequest) {
        String token = this.systemUserTokenProvider.getSystemUserToken();

        Request request = new Request.Builder()
                .url(joinPaths(baseUrl, "/graphql"))
                .header(NAV_PERSONIDENT_HEADER, fnr)
                .header("Authorization", "Bearer " + token)
                .header(NAV_CONSUMER_TOKEN_HEADER, "Bearer " + token)
                .header(TEMA_HEADER, OPPFOLGING_TEMA_HEADERVERDI)
                .method("POST", toJsonRequestBody(pdlHentPersonRequest))
                .build();
        try (Response response = baseClient().newCall(request).execute()) {
            return getBodyStr(response).orElseThrow(RuntimeException::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String hentIdenterQuery() {
        return hentRessursfil("pdl/hentIdenter.graphql");
    }

    private String hentPersonQuery() {
       return hentRessursfil("pdl/hentPerson.graphql");
    }

    private String hentGeografisktilknytningQuery() {
        return hentRessursfil("pdl/hentGeografiskTilknytning.graphql");
    }

    private String hentRessursfil(String sti) {
        ClassLoader classLoader = PdlOppslagClient.class.getClassLoader();
        try (InputStream resourceStream = classLoader.getResourceAsStream(sti)) {
            return new String(resourceStream.readAllBytes(), StandardCharsets.UTF_8).replaceAll("[\n\r]]", "");
        } catch (IOException e) {
            throw new RuntimeException("Integrasjon mot PDL ble ikke gjennomført pga. feil ved lesing av query", e);
        }
    }

    private void validateResponse(PdlResponse response) {
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

    private static class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {

            return Optional.ofNullable(json.getAsJsonPrimitive().getAsString())
                    .map(LocalDate::parse)
                    .orElse(null);
        }
    }
}
