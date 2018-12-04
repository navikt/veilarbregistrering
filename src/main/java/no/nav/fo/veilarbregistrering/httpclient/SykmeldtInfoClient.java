package no.nav.fo.veilarbregistrering.httpclient;

import lombok.extern.slf4j.Slf4j;
import no.nav.fo.veilarbregistrering.domain.SykmeldtInfoData;
import no.nav.sbl.rest.RestUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;

import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static no.nav.sbl.rest.RestUtils.withClient;

@Slf4j
public class SykmeldtInfoClient extends BaseClient {

    public static final String INFOTRYGDAPI_URL_PROPERTY_NAME = "http://infotrygd-sykepenger.default.svc.cluster.local";

    @Inject
    public SykmeldtInfoClient(Provider<HttpServletRequest> httpServletRequestProvider) {
        super(INFOTRYGDAPI_URL_PROPERTY_NAME, httpServletRequestProvider);
    }

    public SykmeldtInfoData hentSykmeldtInfoData(String fnr) {
        String cookies = httpServletRequestProvider.get().getHeader(COOKIE);
        return getSykeforloepMetadata(baseUrl + "/hentMaksdato?fnr=" + fnr , cookies, SykmeldtInfoData.class);
    }

    private static <T> T getSykeforloepMetadata(String url, String cookies, Class<T> returnType) {
        try {
            log.info ("Kaller infotrygd-sykepenger på url : " + url);
            return withClient(RestUtils.RestConfig.builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                    c -> c.target(url)
                            .request()
                            .header(COOKIE, cookies)
                            .get(returnType));
        } catch (Exception e) {
            log.error("Feil ved kall til tjeneste " + e);
            throw new InternalServerErrorException();
        }
    }
}