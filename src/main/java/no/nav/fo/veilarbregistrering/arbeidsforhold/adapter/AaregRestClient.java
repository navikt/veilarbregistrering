package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.sbl.rest.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static no.nav.log.MDCConstants.MDC_CALL_ID;

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
    private  static final String NAV_PERSONIDENT = "Nav-Personident";

    private final String NAV_CALL_ID_HEADER = "Nav-Call-Id";

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
        try {
            String jsonResponse = utforRequest(fnr);
            return parse(jsonResponse);

        } catch (BadRequestException e) {
            throw new RuntimeException("Ugyldig input", e);

        } catch (NotAuthorizedException e) {
            throw new RuntimeException("Token mangler eller er ugyldig", e);

        } catch (ForbiddenException e) {
            throw new RuntimeException("Ingen tilgang til forespurt ressurs", e);

        } catch (NotFoundException e) {
            LOG.warn("Søk på arbeidforhold gav ingen treff", e);
            return Collections.emptyList();

        } catch (RuntimeException e) {
            throw new RuntimeException("Noe gikk galt mot Aareg", e);
        }
    }

    protected String utforRequest(Foedselsnummer fnr) {
        String token = this.systemUserTokenProvider.getSystemUserAccessToken();
        return RestUtils.createClient()
                .target(baseUrl + "/v1/arbeidstaker/arbeidsforhold")
                .queryParam("regelverk=A_ORDNINGEN")
                .request(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + token)
                .header(NAV_CONSUMER_TOKEN, "Bearer " + token)
                .header(NAV_PERSONIDENT, fnr.stringValue())
                .header(NAV_CALL_ID_HEADER, MDC.get(MDC_CALL_ID))
                .get(String.class);
    }

    private static List<ArbeidsforholdDto> parse(String json) {
        return GSON.fromJson(json, new TypeToken<List<ArbeidsforholdDto>>(){}.getType());
    }
}