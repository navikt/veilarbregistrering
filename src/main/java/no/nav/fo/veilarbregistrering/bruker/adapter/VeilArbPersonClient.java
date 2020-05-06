package no.nav.fo.veilarbregistrering.bruker.adapter;

import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import java.util.Optional;

import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static no.nav.sbl.rest.RestUtils.RestConfig.builder;
import static no.nav.sbl.rest.RestUtils.withClient;

class VeilArbPersonClient {

    private static final Logger LOG = LoggerFactory.getLogger(VeilArbPersonClient.class);

    private static final int HTTP_READ_TIMEOUT = 120000;

    private final String baseUrl;
    private final Provider<HttpServletRequest> httpServletRequestProvider;
    private SystemUserTokenProvider systemUserTokenProvider;

    VeilArbPersonClient(String baseUrl, Provider<HttpServletRequest> httpServletRequestProvider, SystemUserTokenProvider systemUserTokenProvider) {
        this.baseUrl = baseUrl;
        this.httpServletRequestProvider = httpServletRequestProvider;
        this.systemUserTokenProvider = systemUserTokenProvider;
    }

    Optional<GeografiskTilknytningDto> geografisktilknytning(Foedselsnummer foedselsnummer) {
        String cookies = httpServletRequestProvider.get().getHeader(COOKIE);
        try {
            GeografiskTilknytningDto geografiskTilknytningDto = withClient(builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                    c -> c.target(baseUrl + "/person/geografisktilknytning?fnr=" + foedselsnummer.stringValue())
                            .request()
                            .header(COOKIE, cookies)
                            .header("SystemAuthorization", this.systemUserTokenProvider.getSystemUserAccessToken())
                            .get(GeografiskTilknytningDto.class));

            return geografiskTilknytningDto != null && geografiskTilknytningDto.getGeografiskTilknytning() != null
                    ? Optional.of(geografiskTilknytningDto) : Optional.empty();

        } catch (NotFoundException e) {
            LOG.warn("Fant ikke geografisk tilknytning for bruker.", e);
            return Optional.empty();

        } catch (ForbiddenException e) {
            throw new RuntimeException("Bruker har ikke tilgang på å hente ut geografisk tilknytning fra VeilArbPerson-tjenesten.", e);

        } catch (Exception e) {
            throw new RuntimeException("Feil ved kall til VeilArbPerson-tjenesten.", e);
        }
    }
}
