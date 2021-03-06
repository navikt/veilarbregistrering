package no.nav.fo.veilarbregistrering.sykemelding.adapter;

import kotlin.Pair;
import no.nav.common.auth.utils.TokenUtils;
import no.nav.common.health.HealthCheck;
import no.nav.common.health.HealthCheckResult;
import no.nav.common.health.HealthCheckUtils;
import no.nav.common.rest.client.RestClient;
import no.nav.common.rest.client.RestUtils;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.http.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static no.nav.common.auth.Constants.AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME;
import static no.nav.common.utils.UrlUtils.joinPaths;
import static no.nav.fo.veilarbregistrering.config.RequestContext.servletRequest;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.COOKIE;

public class SykmeldtInfoClient implements HealthCheck {

    private static final int HTTP_READ_TIMEOUT = 120000;

    private final String baseUrl;
    private final OkHttpClient client;

    public SykmeldtInfoClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = RestClient.baseClientBuilder().readTimeout(HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS).build();
    }

    public InfotrygdData hentSykmeldtInfoData(Foedselsnummer fnr) {
        return getSykeforloepMetadata(baseUrl + "/hentMaksdato?fnr=" + fnr.stringValue());
    }

    private InfotrygdData getSykeforloepMetadata(String url) {
        HttpServletRequest servletRequest = servletRequest();

        Request request = new Request.Builder()
                .url(url)
                .headers(Headers.buildHeaders(List.of(
                        new Pair<>(COOKIE, servletRequest.getHeader(COOKIE)),
                        new Pair<>(AUTHORIZATION, "Bearer " + getToken(servletRequest))
                        )))
                .build();

        try (Response response = client.newCall(request).execute()) {
            return RestUtils.parseJsonResponseOrThrow(response, InfotrygdData.class);
        } catch (IOException e) {
            throw new InternalServerErrorException("Hent maksdato fra Infotrygd feilet.", e);
        }
    }

    private String getToken(HttpServletRequest request) {
        Optional<String> tokenFromCookie = getTokenFromCookie(request, AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME);
        return tokenFromCookie.orElseGet(() -> TokenUtils.getTokenFromHeader(request).orElse(null));
    }

    private Optional<String> getTokenFromCookie(HttpServletRequest request, String cookieName) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays
                        .stream(cookies)
                        .filter(cookie -> cookie.getName().equals(cookieName) && cookie.getValue() != null)
                        .findFirst()
                        .map(Cookie::getValue));
    }

    @Override
    public HealthCheckResult checkHealth() {
        return HealthCheckUtils.pingUrl(joinPaths(baseUrl, "/rest/internal/isAlive"), client);
    }
}
