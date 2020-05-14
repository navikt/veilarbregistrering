package no.nav.fo.veilarbregistrering.bruker.krr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.log.MDCConstants;
import no.nav.sbl.rest.RestUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ws.rs.NotFoundException;
import java.util.Optional;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

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
        try {
            String jsonResponse = RestUtils.withClient(c ->
                    c.target(baseUrl + "v1/personer/kontaktinformasjon")
                            .queryParam("inkluderSikkerDigitalPost", "false")
                            .request()
                            .header(AUTHORIZATION, "Bearer " + systemUserTokenProvider.getSystemUserAccessToken())
                            .header("Nav-Call-Id", MDC.get(MDCConstants.MDC_CALL_ID))
                            .header("Nav-Consumer-Id", "srvveilarbregistrering")
                            .header("Nav-Personidenter", foedselsnummer.stringValue())
                            .get(String.class));

            kontaktinfoDto = map(jsonResponse, foedselsnummer);

        } catch (NotFoundException e) {
            LOG.warn("Fant ikke kontaktinfo på person i kontakt og reservasjonsregisteret", e);
            return Optional.empty();
        }

        return Optional.of(kontaktinfoDto);
    }

    /**
     * Benytter JSONObject til mapping i parallell med GSON pga. dynamisk json.
     */
    static KrrKontaktinfoDto map(String jsonResponse, Foedselsnummer foedselsnummer) {
        JSONObject kontaktinfo = new JSONObject(jsonResponse)
                .getJSONObject("kontaktinfo")
                .getJSONObject(foedselsnummer.stringValue());

        return gson.fromJson(kontaktinfo.toString(), KrrKontaktinfoDto.class);
    }
}
