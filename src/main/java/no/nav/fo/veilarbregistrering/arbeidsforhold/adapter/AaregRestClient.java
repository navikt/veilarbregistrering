package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import no.nav.common.rest.client.RestClient;
import no.nav.common.rest.client.RestUtils;
import no.nav.common.sts.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.nav.common.log.MDCConstants.MDC_CALL_ID;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


class AaregRestClient {

    private static final Logger LOG = LoggerFactory.getLogger(AaregRestClient.class);

    private static final Gson GSON = new GsonBuilder().create();

    /**
     * Token for konsument fra Nav Security Token Service (STS) - brukes til autentisering
     * og autorisasjon for tjenestekall - angis med ‘Bearer + mellomrom + token’
     * */
    private static final String NAV_CONSUMER_TOKEN = "Nav-Consumer-Token";

    /**
     * Identifikator for arbeidstaker (FNR/DNR/Aktør-id)
     */
    private static final String NAV_PERSONIDENT = "Nav-Personident";

    private static final String NAV_CALL_ID_HEADER = "Nav-Call-Id";

    private final String baseUrl;
    private final SystemUserTokenProvider systemUserTokenProvider;

    AaregRestClient(String baseUrl, SystemUserTokenProvider systemUserTokenProvider) {
        this.baseUrl = baseUrl;
        this.systemUserTokenProvider = systemUserTokenProvider;
    }

    /**
     * "Finn arbeidsforhold (detaljer) per arbeidstaker"
     */
    List<ArbeidsforholdDto> finnArbeidsforhold(Foedselsnummer fnr) {
        String response = utforRequest(fnr);
        return parse(response);
    }

    protected String utforRequest(Foedselsnummer fnr) {
        String token = this.systemUserTokenProvider.getSystemUserToken();
        try {
            Response response = RestClient.baseClient().newCall(
                    new Request.Builder()
                            .url(HttpUrl.parse(baseUrl).newBuilder()
                                    .addPathSegments("v1/arbeidstaker/arbeidsforhold")
                                    .addQueryParameter("regelverk", "A_ORDNINGEN")
                                    .build())
                            .header(ACCEPT, APPLICATION_JSON_VALUE)
                            .header(AUTHORIZATION, "Bearer " + "TOKEN"//TODO SubjectHandler.getSsoToken(OIDC)
                                    //.orElseThrow(() -> new IllegalStateException("Fant ikke SSO token via OIDC")))
                            )
                            .header(NAV_CONSUMER_TOKEN, "Bearer " + token)
                            .header(NAV_PERSONIDENT, fnr.stringValue())
                            .header(NAV_CALL_ID_HEADER, MDC.get(MDC_CALL_ID))
                            .build())
                    .execute();
            return behandleResponse(response);
        } catch (IOException e) {
            throw new RuntimeException("Noe gikk galt mot Aareg", e);
        }
    }

    private String behandleResponse(Response response) throws IOException {
        if (response.isSuccessful()) {
            return RestUtils.getBodyStr(response).orElseThrow(RuntimeException::new);
        } else {
            String feilmelding = Map.of(
                    BAD_REQUEST, "Ugyldig input",
                    UNAUTHORIZED, "Token mangler eller er ugyldig",
                    FORBIDDEN, "Ingen tilgang til forespurt ressurs",
                    NOT_FOUND, "Søk på arbeidforhold gav ingen treff"
            ).getOrDefault(HttpStatus.resolve(response.code()), "Noe gikk galt mot Aareg");
            throw new RuntimeException(feilmelding);
        }
    }

    private static List<ArbeidsforholdDto> parse(String json) {
        return GSON.fromJson(json, new TypeToken<List<ArbeidsforholdDto>>(){}.getType());
    }
}