package no.nav.fo.veilarbregistrering.bruker.adapter;

import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.httpclient.BaseClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static no.nav.sbl.rest.RestUtils.RestConfig.builder;
import static no.nav.sbl.rest.RestUtils.withClient;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class VeilArbPersonClient extends BaseClient {

    private static final Logger LOG = LoggerFactory.getLogger(VeilArbPersonClient.class);

    static final String PERSON_API_PROPERTY_NAME = "VEILARBPERSONGAPI_URL";

    private SystemUserTokenProvider systemUserTokenProvider;

    public VeilArbPersonClient(Provider<HttpServletRequest> httpServletRequestProvider) {
        super(getRequiredProperty(PERSON_API_PROPERTY_NAME), httpServletRequestProvider);
    }

    public String geografisktilknytning(Foedselsnummer foedselsnummer) {
        String cookies = httpServletRequestProvider.get().getHeader(COOKIE);
        try {
            return withClient(builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                    c -> c.target(baseUrl + "/person/geografisktilknytning?fnr=" + foedselsnummer.srringValue())
                            .request()
                            .header(COOKIE, cookies)
                            .get(String.class));
        } catch (ForbiddenException e) {
            LOG.error("Ingen tilgang " + e);
            Response response = e.getResponse();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            LOG.error("Feil ved kall til tjeneste " + e);
            throw new InternalServerErrorException();
        }
    }

    public void settSystemUserTokenProvider(SystemUserTokenProvider systemUserTokenProvider) {
        this.systemUserTokenProvider = systemUserTokenProvider;
    }
}
