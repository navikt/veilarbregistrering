package no.nav.fo.veilarbregistrering.bruker.krr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.nav.common.rest.client.RestUtils;
import no.nav.common.sts.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.Optional;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static no.nav.common.log.MDCConstants.MDC_CALL_ID;
import static no.nav.common.rest.client.RestClient.baseClient;
import static org.h2.util.IntIntHashMap.NOT_FOUND;

class KrrClient {

    private static final Logger LOG = LoggerFactory.getLogger(KrrClient.class);

    private final SystemUserTokenProvider systemUserTokenProvider;
    private final String baseUrl;

    private static final Gson gson = new GsonBuilder().create();

    KrrClient(String baseUrl, SystemUserTokenProvider systemUserTokenProvider) {
        this.baseUrl = baseUrl;
        this.systemUserTokenProvider = systemUserTokenProvider;
    }

    Optional<KrrKontaktinfoDto> hentKontaktinfo(Foedselsnummer foedselsnummer) {
        KrrKontaktinfoDto kontaktinfoDto;
        Request request = new Request.Builder()
                .url(
                        HttpUrl.parse(baseUrl).newBuilder()
                                .addPathSegments("v1/personer/kontaktinformasjon")
                                .addQueryParameter("inkluderSikkerDigitalPost", "false")
                                .build())
                .header(AUTHORIZATION, "Bearer " + systemUserTokenProvider.getSystemUserToken())
                .header("Nav-Call-Id", MDC.get(MDC_CALL_ID))
                .header("Nav-Consumer-Id", "srvveilarbregistrering")
                .header("Nav-Personidenter", foedselsnummer.stringValue())
                .build();
        try {
            Response response = baseClient().newCall(request).execute();

            if (!response.isSuccessful() || response.code() == NOT_FOUND) {
                LOG.warn("Fant ikke kontaktinfo på person i kontakt og reservasjonsregisteret");
                return Optional.empty();
            }

            kontaktinfoDto = parse(RestUtils.getBodyStr(response).orElseThrow(), foedselsnummer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NotFoundException e) {
            LOG.warn("Fant ikke kontaktinfo på person i kontakt og reservasjonsregisteret");
            return Optional.empty();
        }

        return Optional.of(kontaktinfoDto);
    }

    /**
     * Benytter JSONObject til parsing i parallell med GSON pga. dynamisk json.
     */
    static KrrKontaktinfoDto parse(String jsonResponse, Foedselsnummer foedselsnummer) {
        if (new JSONObject(jsonResponse).has("kontaktinfo")) {
            JSONObject kontaktinfo = new JSONObject(jsonResponse)
                    .getJSONObject("kontaktinfo")
                    .getJSONObject(foedselsnummer.stringValue());

            return gson.fromJson(kontaktinfo.toString(), KrrKontaktinfoDto.class);
        }

        if (new JSONObject(jsonResponse).has("feil")) {
            JSONObject response = new JSONObject(jsonResponse)
                    .getJSONObject("feil")
                    .getJSONObject(foedselsnummer.stringValue());

            KrrFeilDto feil = gson.fromJson(response.toString(), KrrFeilDto.class);

            if ("Ingen kontaktinformasjon er registrert på personen".equals(feil.getMelding())) {
                throw new NotFoundException(feil.getMelding());
            }

            throw new RuntimeException(String.format("Henting av kontaktinfo fra KRR feilet: %s", feil.getMelding()));
        }
        throw new RuntimeException("Ukjent feil");
    }
}
