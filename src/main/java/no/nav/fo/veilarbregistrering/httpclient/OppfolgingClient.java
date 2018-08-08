package no.nav.fo.veilarbregistrering.httpclient;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import no.nav.fo.veilarbregistrering.domain.AktiverBrukerData;
import no.nav.fo.veilarbregistrering.domain.Fnr;
import no.nav.fo.veilarbregistrering.domain.OppfolgingStatusData;
import no.nav.sbl.rest.RestUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static no.nav.sbl.rest.RestUtils.withClient;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Slf4j
public class OppfolgingClient {

    public static final String VEILARBOPPFOLGINGAPI_URL_PROPERTY_NAME = "VEILARBOPPFOLGINGAPI_URL";

    private final String baseUrl;
    private final SystemUserAuthorizationInterceptor systemUserAuthorizationInterceptor;
    private final Provider<HttpServletRequest> httpServletRequestProvider;

    @Inject
    public OppfolgingClient(Provider<HttpServletRequest> httpServletRequestProvider) {
        this(getRequiredProperty(VEILARBOPPFOLGINGAPI_URL_PROPERTY_NAME), new SystemUserAuthorizationInterceptor(), httpServletRequestProvider);
    }

    public OppfolgingClient(String baseUrl, SystemUserAuthorizationInterceptor systemUserAuthorizationInterceptor, Provider<HttpServletRequest> httpServletRequestProvider) {
        this.baseUrl = baseUrl;
        this.systemUserAuthorizationInterceptor = systemUserAuthorizationInterceptor;
        this.httpServletRequestProvider = httpServletRequestProvider;
    }

    public void reaktiverBruker(String fnr) {
        withClient(
                RestUtils.RestConfig.builder().readTimeout(120000).build()
                , c -> postBrukerReAktivering(fnr, c)
        );
    }

    public void aktiverBruker(AktiverBrukerData aktiverBrukerData) {
        withClient(
                RestUtils.RestConfig.builder().readTimeout(120000).build()
                , c -> postBrukerAktivering(aktiverBrukerData, c)
        );
    }

    public OppfolgingStatusData hentOppfolgingsstatus(String fnr) {
        String cookies = httpServletRequestProvider.get().getHeader(COOKIE);
        return getOppfolging(baseUrl + "/oppfolging?fnr=" + fnr, cookies, OppfolgingStatusData.class);
    }

    private int postBrukerReAktivering(String fnr, Client client) {
        String url = baseUrl + "/oppfolging/reaktiverbruker";
        Response response = client.target(url)
                .register(systemUserAuthorizationInterceptor)
                .request()
                .post(Entity.json(new Fnr(fnr)));

        return behandleHttpResponse(response, url);
    }

    private int postBrukerAktivering(AktiverBrukerData aktiverBrukerData, Client client) {
        String url = baseUrl + "/oppfolging/aktiverbruker";
        Response response = client.target(url)
                .register(systemUserAuthorizationInterceptor)
                .request()
                .post(Entity.json(aktiverBrukerData));

        return behandleHttpResponse(response, url);
    }

    private int behandleHttpResponse(Response response, String url) {
        int status = response.getStatus();

        if (status == 204) {
            return status;
        } else if (status == 500) {
            log.error("Feil ved kall mot VeilArbOppfolging : {}, response : {}", url, response);
            throw new WebApplicationException(response);
        } else {
            throw new RuntimeException("Uventet respons (" + status + ") ved kall mot mot " + url);
        }
    }

    private static <T> T getOppfolging(String url, String cookies, Class<T> returnType) {
        return Try.of(() -> withClient(c -> c.target(url).request().header(COOKIE, cookies).get(returnType)))
                .onFailure((e) -> {
                    log.error("Feil ved kall til Oppfølging {}", url, e);
                    throw new InternalServerErrorException();
                })
                .get();

    }
}