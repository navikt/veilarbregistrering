package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.config.GammelSystemUserTokenProvider;
import no.nav.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static no.nav.sbl.rest.RestUtils.RestConfig.builder;
import static no.nav.sbl.rest.RestUtils.withClient;

public class OppfolgingClient {

    private static final Logger LOG = LoggerFactory.getLogger(OppfolgingClient.class);

    private static final int HTTP_READ_TIMEOUT = 120000;

    private final String baseUrl;
    private final Provider<HttpServletRequest> httpServletRequestProvider;

    private final SystemUserTokenProvider systemUserTokenProvider;
    private final GammelSystemUserTokenProvider gammelSystemUserTokenProvider;

    public OppfolgingClient(
            String baseUrl,
            Provider<HttpServletRequest> httpServletRequestProvider,
            SystemUserTokenProvider systemUserTokenProvider,
            GammelSystemUserTokenProvider gammelSystemUserTokenProvider) {
        this.baseUrl = baseUrl;
        this.httpServletRequestProvider = httpServletRequestProvider;
        this.systemUserTokenProvider = systemUserTokenProvider;
        this.gammelSystemUserTokenProvider = gammelSystemUserTokenProvider;
    }

    public OppfolgingStatusData hentOppfolgingsstatus(Foedselsnummer fnr) {
        String cookies = httpServletRequestProvider.get().getHeader(COOKIE);
        try {
            return withClient(builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                    c -> c.target(baseUrl + "/oppfolging?fnr=" + fnr.stringValue())
                            .request()
                            .header(COOKIE, cookies)
                            .get(OppfolgingStatusData.class));
        } catch (ForbiddenException e) {
            LOG.error("Ingen tilgang " + e);
            Response response = e.getResponse();
            throw new WebApplicationException(response);
        } catch (Exception e) {
            LOG.error("Feil ved kall til tjeneste " + e);
            throw new InternalServerErrorException();
        }
    }

    public void reaktiverBruker(Foedselsnummer fnr) {
        withClient(
                builder().readTimeout(HTTP_READ_TIMEOUT).build()
                , c -> postBrukerReAktivering(fnr, c)
        );
    }

    private int postBrukerReAktivering(Foedselsnummer fnr, Client client) {
        String url = baseUrl + "/oppfolging/reaktiverbruker";
        Response response = buildSystemAuthorizationRequestWithUrl(client, url).post(json(new Fnr(fnr.stringValue())));
        return behandleHttpResponse(response, url);
    }

    public void aktiverBruker(AktiverBrukerData aktiverBrukerData) {
        withClient(
                builder().readTimeout(HTTP_READ_TIMEOUT).build()
                , c -> postBrukerAktivering(aktiverBrukerData, c)
        );
    }

    private int postBrukerAktivering(AktiverBrukerData aktiverBrukerData, Client client) {
        String url = baseUrl + "/oppfolging/aktiverbruker";
        Response response = buildSystemAuthorizationRequestWithUrl(client, url).post(json(aktiverBrukerData));

        return behandleHttpResponse(response, url);
    }

    void settOppfolgingSykmeldt(SykmeldtBrukerType sykmeldtBrukerType, Foedselsnummer fnr) {
        withClient(
                builder().readTimeout(HTTP_READ_TIMEOUT).build()
                , c -> postOppfolgingSykmeldt(sykmeldtBrukerType, fnr, c)
        );
    }

    private int postOppfolgingSykmeldt(SykmeldtBrukerType sykmeldtBrukerType, Foedselsnummer fnr, Client client) {
        String url = baseUrl + "/oppfolging/aktiverSykmeldt/?fnr=" + fnr.stringValue();
        Response response = buildSystemAuthorizationRequestWithUrl(client, url).post(json(sykmeldtBrukerType));
        return behandleHttpResponse(response, url);
    }

    private Builder buildSystemAuthorizationRequestWithUrl(Client client, String url) {
        return client.target(url)
                .request()
                .header("SystemAuthorization", this.gammelSystemUserTokenProvider.getToken())
                .header(AUTHORIZATION, "Bearer " + this.gammelSystemUserTokenProvider.getToken());
    }

    private int behandleHttpResponse(Response response, String url) {
        int status = response.getStatus();

        if (status == 204) {
            return status;
        } else if (status == 403) {
            LOG.warn("Feil ved kall mot: {}, response : {}", url, response);
            throw new WebApplicationException(response);
        } else {
            throw new RuntimeException(String.format("Uventet respons (%s) ved kall mot %s", status, url));
        }
    }

    static AktiverBrukerFeilDto parseResponse(String json) {
        return JsonUtils.fromJson(json, AktiverBrukerFeilDto.class);
    }
}
