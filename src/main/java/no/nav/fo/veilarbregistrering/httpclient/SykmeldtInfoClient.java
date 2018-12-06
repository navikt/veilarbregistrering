package no.nav.fo.veilarbregistrering.httpclient;

import lombok.extern.slf4j.Slf4j;
import no.nav.fo.veilarbregistrering.domain.InfotrygdData;
import no.nav.fo.veilarbregistrering.domain.SykmeldtInfoData;
import no.nav.sbl.rest.RestUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;

import static io.vavr.collection.Array.*;
import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static no.nav.brukerdialog.security.oidc.provider.AzureADB2CProvider.AZUREADB2C_OIDC_COOKIE_NAME;
import static no.nav.sbl.rest.RestUtils.withClient;

@Slf4j
public class SykmeldtInfoClient extends BaseClient {

    public static final String INFOTRYGDAPI_URL_PROPERTY_NAME = "http://infotrygd-sykepenger.default.svc.nais.local";

    @Inject
    public SykmeldtInfoClient(Provider<HttpServletRequest> httpServletRequestProvider) {
        super(INFOTRYGDAPI_URL_PROPERTY_NAME, httpServletRequestProvider);
    }

    public InfotrygdData hentSykmeldtInfoData(String fnr) {
        return getSykeforloepMetadata(baseUrl + "/hentMaksdato?fnr=" + fnr);
    }

    private InfotrygdData getSykeforloepMetadata(String url) {

        try {
            log.info("Kaller infotrygd-sykepenger på url : " + url);
            return withClient(RestUtils.RestConfig.builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                    c -> c.target(url)
                            .request()
                            .header(COOKIE, httpServletRequestProvider.get().getHeader(COOKIE))
                            .header("Authorization", "Bearer " + getAuthHeader())
                            .get(InfotrygdData.class));
        } catch (Exception e) {
            log.error("Feil ved kall til tjeneste " + e);
            throw new InternalServerErrorException();
        }
    }

    private String getAuthHeader() {
        Cookie cookie = of(httpServletRequestProvider.get().getCookies())
                .filter(c -> AZUREADB2C_OIDC_COOKIE_NAME.equals(c.getName()))
                .get();
        if (cookie != null) {
            return cookie.getValue();
        }
        return "";
    }

}