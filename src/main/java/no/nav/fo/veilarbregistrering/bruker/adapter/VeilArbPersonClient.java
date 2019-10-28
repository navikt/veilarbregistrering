package no.nav.fo.veilarbregistrering.bruker.adapter;

import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.httpclient.BaseClient;
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

class VeilArbPersonClient extends BaseClient {

    private static final Logger LOG = LoggerFactory.getLogger(VeilArbPersonClient.class);

    private SystemUserTokenProvider systemUserTokenProvider;

    VeilArbPersonClient(String baseUrl, Provider<HttpServletRequest> httpServletRequestProvider) {
        super(baseUrl, httpServletRequestProvider);
    }

    Optional<String> geografisktilknytning(Foedselsnummer foedselsnummer) {
        String cookies = httpServletRequestProvider.get().getHeader(COOKIE);
        try {
            return Optional.of(withClient(builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                    c -> c.target(baseUrl + "/person/geografisktilknytning?fnr=" + foedselsnummer.stringValue())
                            .request()
                            .header(COOKIE, cookies)
                            .header("SystemAuthorization",
                                    (this.systemUserTokenProvider == null ? new SystemUserTokenProvider() : this.systemUserTokenProvider))
                            .get(String.class)));
        } catch (NotFoundException e) {
            LOG.warn("Fant ikke geografisk tilknytning for bruker.", e);
            return Optional.empty();

        } catch (ForbiddenException e) {
            throw new RuntimeException("Bruker har ikke tilgang på å hente ut geografisk tilknytning fra VeilArbPerson-tjenesten.", e);

        } catch (Exception e) {
            throw new RuntimeException("Feil ved kall til VeilArbPerson-tjenesten.", e);
        }
    }

    void settSystemUserTokenProvider(SystemUserTokenProvider systemUserTokenProvider) {
        this.systemUserTokenProvider = systemUserTokenProvider;
    }
}
