package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import lombok.extern.slf4j.Slf4j;
import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider;

import no.nav.fo.veilarbregistrering.httpclient.BaseClient;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static no.nav.sbl.rest.RestUtils.RestConfig.builder;
import static no.nav.sbl.rest.RestUtils.withClient;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Slf4j
public class OppfolgingClient extends BaseClient {

    public static final String OPPFOLGING_API_PROPERTY_NAME = "VEILARBOPPFOLGINGAPI_URL";
    private SystemUserTokenProvider systemUserTokenProvider;

    @Inject
    public OppfolgingClient(Provider<HttpServletRequest> httpServletRequestProvider) {
        super(getRequiredProperty(OPPFOLGING_API_PROPERTY_NAME), httpServletRequestProvider);
    }

    public void reaktiverBruker(String fnr) {
        withClient(
                builder().readTimeout(HTTP_READ_TIMEOUT).build()
                , c -> postBrukerReAktivering(fnr, c)
        );
    }

    public void aktiverBruker(AktiverBrukerData aktiverBrukerData) {
        withClient(
                builder().readTimeout(HTTP_READ_TIMEOUT).build()
                , c -> postBrukerAktivering(aktiverBrukerData, c)
        );
    }

    public OppfolgingStatusData hentOppfolgingsstatus(String fnr) {
        String cookies = httpServletRequestProvider.get().getHeader(COOKIE);
        try {
            return withClient(builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                    c -> c.target(baseUrl + "/oppfolging?brukArenaDirekte=true&fnr=" + fnr)
                            .request()
                            .header(COOKIE, cookies)
                            .get(OppfolgingStatusData.class));
        } catch (ForbiddenException e) {
            log.error("Ingen tilgang " + e);
            Response response = e.getResponse();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            log.error("Feil ved kall til tjeneste " + e);
            throw new InternalServerErrorException();
        }
    }

    public void settOppfolgingSykmeldt(SykmeldtBrukerType sykmeldtBrukerType, String fnr) {
        withClient(
                builder().readTimeout(HTTP_READ_TIMEOUT).build()
                , c -> postOppfolgingSykmeldt(sykmeldtBrukerType, fnr, c)
        );
    }

    private int postBrukerReAktivering(String fnr, Client client) {
        String url = baseUrl + "/oppfolging/reaktiverbruker";
        Response response = buildSystemAuthorizationRequestWithUrl(client, url).post(json(new Fnr(fnr)));
        return behandleHttpResponse(response, url);
    }

    private int postBrukerAktivering(AktiverBrukerData aktiverBrukerData, Client client) {
        String url = baseUrl + "/oppfolging/aktiverbruker";
        Response response = buildSystemAuthorizationRequestWithUrl(client, url).post(json(aktiverBrukerData));
        return behandleHttpResponse(response, url);
    }

    private int postOppfolgingSykmeldt(SykmeldtBrukerType sykmeldtBrukerType, String fnr, Client client) {
        String url = baseUrl + "/oppfolging/aktiverSykmeldt/?fnr=" + fnr;
        Response response = buildSystemAuthorizationRequestWithUrl(client, url).post(json(sykmeldtBrukerType));
        return behandleHttpResponse(response, url);
    }

    private Builder buildSystemAuthorizationRequestWithUrl(Client client, String url) {
        String cookies = httpServletRequestProvider.get().getHeader(COOKIE);
        return client.target(url)
                .request()
                .header(COOKIE, cookies)
                .header("SystemAuthorization",
                        (this.systemUserTokenProvider==null? new SystemUserTokenProvider() : this.systemUserTokenProvider)
                                .getToken());
    }

    public void settSystemUserTokenProvider(SystemUserTokenProvider systemUserTokenProvider) {
        this.systemUserTokenProvider = systemUserTokenProvider;
    }
}
