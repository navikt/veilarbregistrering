package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import no.nav.common.health.HealthCheck;
import no.nav.common.health.HealthCheckResult;
import no.nav.common.health.HealthCheckUtils;
import no.nav.common.rest.client.RestClient;
import no.nav.common.rest.client.RestUtils;
import no.nav.common.sts.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.HentOppfolgingStatusException;
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerFeil;
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerResultat;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.MILLIS;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static no.nav.common.utils.UrlUtils.joinPaths;
import static no.nav.fo.veilarbregistrering.config.RequestContext.servletRequest;

public class OppfolgingClient implements HealthCheck {

    private static final Logger LOG = LoggerFactory.getLogger(OppfolgingClient.class);

    private static final int HTTP_READ_TIMEOUT = 120000;

    private final String baseUrl;
    private final OkHttpClient client;

    private final SystemUserTokenProvider systemUserTokenProvider;

    public OppfolgingClient(
            String baseUrl,
            SystemUserTokenProvider systemUserTokenProvider) {
        this.baseUrl = baseUrl;
        this.systemUserTokenProvider = systemUserTokenProvider;
        this.client = RestClient.baseClient().newBuilder().readTimeout(Duration.of(HTTP_READ_TIMEOUT, MILLIS)).build();
    }

    public OppfolgingStatusData hentOppfolgingsstatus(Foedselsnummer fnr) {
        Request request = new Request.Builder()
                .url(HttpUrl.parse(baseUrl).newBuilder()
                        .addPathSegment("oppfolging")
                        .addQueryParameter("fnr", fnr.stringValue())
                        .build())
                .header(COOKIE, servletRequest().getHeader(COOKIE))
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new HentOppfolgingStatusException("Hent oppfølgingstatus feilet med status: " + response.code());
            }
            return RestUtils.parseJsonResponseOrThrow(response, OppfolgingStatusData.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AktiverBrukerResultat reaktiverBruker(Foedselsnummer fnr) {
        Request request = buildSystemAuthorizationRequest()
                .url(baseUrl + "/oppfolging/reaktiverbruker")
                .method("POST", RestUtils.toJsonRequestBody(new Fnr(fnr.stringValue())))
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            return behandleHttpResponse(response, baseUrl + "/oppfolging/reaktiverbruker");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AktiverBrukerResultat aktiverBruker(AktiverBrukerData aktiverBrukerData) {
        Request request = buildSystemAuthorizationRequest()
                .url(baseUrl + "/oppfolging/aktiverbruker")
                .method("POST", RestUtils.toJsonRequestBody(aktiverBrukerData))
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            return behandleHttpResponse(response, baseUrl + "/oppfolging/aktiverbruker");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void settOppfolgingSykmeldt(SykmeldtBrukerType sykmeldtBrukerType, Foedselsnummer fnr) {
        Request request = buildSystemAuthorizationRequest()
                .url(HttpUrl.parse(baseUrl).newBuilder()
                        .addPathSegments("oppfolging/aktiverSykmeldt/")
                        .addQueryParameter("fnr", fnr.stringValue())
                        .build())
                .method("POST", RestUtils.toJsonRequestBody(sykmeldtBrukerType))
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            behandleHttpResponse(response, HttpUrl.parse(baseUrl).newBuilder().addPathSegments("oppfolging/aktiverSykmeldt/")
                    .addQueryParameter("fnr", fnr.stringValue())
                    .build().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Request.Builder buildSystemAuthorizationRequest() {
        return new Request.Builder()
                .header("SystemAuthorization", this.systemUserTokenProvider.getSystemUserToken())
                .header(AUTHORIZATION, "Bearer " + this.systemUserTokenProvider.getSystemUserToken());
    }

    private AktiverBrukerResultat behandleHttpResponse(okhttp3.Response response, String url) throws IOException {
        int status = response.code();

        if (status == 204) {
            return AktiverBrukerResultat.Companion.ok();
        } else if (status == 403) {
            LOG.warn("Feil ved kall mot: {}, response : {}", url, response);
            AktiverBrukerFeilDto aktiverBrukerFeilDto = parse(response);
            return AktiverBrukerResultat.Companion.feilFrom(mapper(aktiverBrukerFeilDto));
        } else {
            throw new RuntimeException(String.format("Uventet respons (%s) ved kall mot %s", status, url));
        }
    }

    private AktiverBrukerFeil mapper(AktiverBrukerFeilDto aktiverBrukerFeilDto) {
        switch (aktiverBrukerFeilDto.getType()) {
            case BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET:
                return AktiverBrukerFeil.BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET;
            case BRUKER_MANGLER_ARBEIDSTILLATELSE:
                return AktiverBrukerFeil.BRUKER_MANGLER_ARBEIDSTILLATELSE;
            case BRUKER_KAN_IKKE_REAKTIVERES:
                return AktiverBrukerFeil.BRUKER_KAN_IKKE_REAKTIVERES;
            case BRUKER_ER_UKJENT:
                return AktiverBrukerFeil.BRUKER_ER_UKJENT;
            default:
                throw new IllegalStateException("Ukjent feil fra Arena: " + aktiverBrukerFeilDto.getType());
        }
    }

    static AktiverBrukerFeilDto parse(okhttp3.Response response) throws IOException {
        return RestUtils.parseJsonResponseOrThrow(response, AktiverBrukerFeilDto.class);
    }

    @Override
    public HealthCheckResult checkHealth() {
        return HealthCheckUtils.pingUrl(joinPaths(baseUrl, "/ping"), client);
    }
}
