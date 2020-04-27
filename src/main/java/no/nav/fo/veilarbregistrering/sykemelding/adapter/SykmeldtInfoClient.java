package no.nav.fo.veilarbregistrering.sykemelding.adapter;

import no.nav.common.oidc.Constants;
import no.nav.common.oidc.utils.TokenUtils;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.httpclient.BaseClient;
import no.nav.sbl.rest.RestUtils;

import javax.inject.Provider;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;

import java.util.Arrays;
import java.util.Optional;

import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static no.nav.common.oidc.Constants.AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME;
import static no.nav.sbl.rest.RestUtils.withClient;

public class SykmeldtInfoClient extends BaseClient {

    public SykmeldtInfoClient(String baseUrl, Provider<HttpServletRequest> httpServletRequestProvider) {
        super(baseUrl, httpServletRequestProvider);
    }

    public InfotrygdData hentSykmeldtInfoData(Foedselsnummer fnr) {
        return getSykeforloepMetadata(baseUrl + "/hentMaksdato?fnr=" + fnr.stringValue());
    }

    private InfotrygdData getSykeforloepMetadata(String url) {
        HttpServletRequest request = httpServletRequestProvider.get();

        try {
            return withClient(RestUtils.RestConfig.builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                    c -> c.target(url)
                            .request()
                            .header(COOKIE, request.getHeader(COOKIE))
                            .header("Authorization", "Bearer " + getToken(request))
                            .get(InfotrygdData.class));
        } catch (Exception e) {
            throw new InternalServerErrorException("Hent maksdato fra Infotrygd feilet.", e);
        }
    }

    private String getToken(HttpServletRequest request) {
        Optional<String> tokenFromCookie = getTokenFromCookie(request, AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME);

        if (tokenFromCookie.isPresent()) {
            return tokenFromCookie.get();
        }

        return TokenUtils.getTokenFromHeader(request).orElse(null);
    }

    private Optional<String> getTokenFromCookie(HttpServletRequest request, String cookieName) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays
                        .stream(cookies)
                        .filter(cookie -> cookie.getName().equals(cookieName) && cookie.getValue() != null)
                        .findFirst()
                        .map(Cookie::getValue)
                );
    }
}
