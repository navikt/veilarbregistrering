package no.nav.fo.veilarbregistrering.httpclient;

import io.vavr.control.Try;
import no.nav.fo.veilarbregistrering.domain.AktivStatus;
import no.nav.fo.veilarbregistrering.domain.AktiverBrukerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Entity;

import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static no.nav.sbl.rest.RestUtils.withClient;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class OppfolgingClient {

    public static final String VEILARBOPPFOLGINGAPI_URL_PROPERTY_NAME = "VEILARBOPPFOLGINGAPI_URL";

    private final String baseUrl;
    private final SystemUserAuthorizationInterceptor systemUserAuthorizationInterceptor;
    private final Provider<HttpServletRequest> httpServletRequestProvider;

    private static final Logger LOG = LoggerFactory.getLogger(OppfolgingClient.class);


    @Inject
    public OppfolgingClient(Provider<HttpServletRequest> httpServletRequestProvider) {
        this(getRequiredProperty(VEILARBOPPFOLGINGAPI_URL_PROPERTY_NAME), new SystemUserAuthorizationInterceptor(), httpServletRequestProvider);
    }

    public OppfolgingClient(String baseUrl, SystemUserAuthorizationInterceptor systemUserAuthorizationInterceptor, Provider<HttpServletRequest> httpServletRequestProvider) {
        this.baseUrl = baseUrl;
        this.systemUserAuthorizationInterceptor = systemUserAuthorizationInterceptor;
        this.httpServletRequestProvider = httpServletRequestProvider;
    }

    public void aktiverBruker(AktiverBrukerData aktiverBrukerData) {
        try {
            withClient(c -> c.target(baseUrl + "/oppfolging/aktiverbruker")
                    .register(systemUserAuthorizationInterceptor)
                    .request()
                    .post(Entity.json(aktiverBrukerData), AktiverBrukerData.class));
        } catch (Exception e) {
            LOG.error("Feil ved aktivering av bruker mot Oppfølging " + e);
            throw new InternalServerErrorException();
        }
    }

    public AktivStatus hentOppfolgingsstatus(String fnr) {
        String cookies = httpServletRequestProvider.get().getHeader(COOKIE);
        return getOppfolging(baseUrl + "/person/" + fnr + "/aktivstatus", cookies, AktivStatus.class);
    }

    private static <T> T getOppfolging(String url, String cookies, Class<T> returnType) {
        return Try.of(() -> withClient(c -> c.target(url).request().header(COOKIE, cookies).get(returnType)))
                .onFailure((e) -> {
                    LOG.error("Feil ved kall til Oppfølging " + url + ", " + e);
                    throw new InternalServerErrorException();
                })
                .get();

    }
}