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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static no.nav.sbl.rest.RestUtils.withClient;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Slf4j
public class OppfolgingClient extends BaseClient {

    public static final String OPPFOLGING_API_PROPERTY_NAME = "VEILARBOPPFOLGINGAPI_URL";

    @Inject
    public OppfolgingClient(Provider<HttpServletRequest> httpServletRequestProvider) {
        super(getRequiredProperty(OPPFOLGING_API_PROPERTY_NAME), httpServletRequestProvider);
    }

    public void reaktiverBruker(String fnr) {
        String cookies = httpServletRequestProvider.get().getHeader(COOKIE);
        withClient(
                RestUtils.RestConfig.builder().readTimeout(HTTP_READ_TIMEOUT).build()
                , c -> postBrukerReAktivering(fnr, c, cookies)
        );
    }

    public void aktiverBruker(AktiverBrukerData aktiverBrukerData) {
        String cookies = httpServletRequestProvider.get().getHeader(COOKIE);
        withClient(
                RestUtils.RestConfig.builder().readTimeout(HTTP_READ_TIMEOUT).build()
                , c -> postBrukerAktivering(aktiverBrukerData, c, cookies)
        );
    }

    public OppfolgingStatusData hentOppfolgingsstatus(String fnr) {
        String cookies = httpServletRequestProvider.get().getHeader(COOKIE);
        return getOppfolging(baseUrl + "/oppfolging?fnr=" + fnr, cookies, OppfolgingStatusData.class);
    }

    public void settOppfolgingSykmeldt(String fnr) {
        String cookies = httpServletRequestProvider.get().getHeader(COOKIE);
        withClient(
                RestUtils.RestConfig.builder().readTimeout(HTTP_READ_TIMEOUT).build()
                , c -> postOppfolgingSykmeldt(fnr, c, cookies)
        );
    }


    private int postBrukerReAktivering(String fnr, Client client, String cookies) {
        String url = baseUrl + "/oppfolging/reaktiverbruker";
        Response response = client.target(url)
                .request()
                .header(COOKIE, cookies)
                .post(Entity.json(new Fnr(fnr)));

        return behandleHttpResponse(response, url);
    }

    private int postBrukerAktivering(AktiverBrukerData aktiverBrukerData, Client client, String cookies) {
        String url = baseUrl + "/oppfolging/aktiverbruker";
        Response response = client.target(url)
                .request()
                .header(COOKIE, cookies)
                .post(Entity.json(aktiverBrukerData));

        return behandleHttpResponse(response, url);
    }

    private int postOppfolgingSykmeldt(String fnr, Client client, String cookies) {
        String url = baseUrl + "/oppfolging/aktiverSykmeldt";
        Response response = client.target(url)
                .request()
                .header(COOKIE, cookies)
                .post(null);

        return behandleHttpResponse(response, url);
    }

    private static <T> T getOppfolging(String url, String cookies, Class<T> returnType) {
        return Try.of(() ->
                withClient(RestUtils.RestConfig.builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                        c -> c.target(url).request().header(COOKIE, cookies).get(returnType)))
                .onFailure((e) -> {
                    log.error("Feil ved kall til Oppfølging {}", url, e);
                    throw new InternalServerErrorException();
                })
                .get();

    }
}