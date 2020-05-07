package no.nav.fo.veilarbregistrering.bruker.krr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.nav.apiapp.feil.Feil;
import no.nav.apiapp.feil.FeilType;
import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.log.MDCConstants;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import no.nav.sbl.rest.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

class KrrClient {

    private static final Logger LOG = LoggerFactory.getLogger(KrrClient.class);

    private final SystemUserTokenProvider systemUserTokenProvider;
    private final UnleashService unleashService;
    private final String baseUrl;

    private final Gson gson = new GsonBuilder().create();

    KrrClient(String baseUrl, SystemUserTokenProvider systemUserTokenProvider, UnleashService unleashService) {
        this.baseUrl = baseUrl;
        this.systemUserTokenProvider = systemUserTokenProvider;
        this.unleashService = unleashService;
    }

    KontaktinfoDto hentKontaktinfo(Foedselsnummer foedselsnummer) {
        KontaktinfoDto kontaktinfoDto;
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

            if (unleashService.isEnabled("veilarbregistrering.kontaktinfo")) {
                LOG.info("JsonResponse: ", jsonResponse);
            }

            kontaktinfoDto = gson.fromJson(jsonResponse, KontaktinfoDto.class);

        } catch (NotAuthorizedException | ForbiddenException e) {
            throw new Feil(FeilType.INGEN_TILGANG, e);
        } catch (NotFoundException e) {
            throw new Feil(FeilType.FINNES_IKKE, e);
        } catch (Exception e) {
            throw new Feil(FeilType.UKJENT, e);
        }

        return kontaktinfoDto;
    }
}
