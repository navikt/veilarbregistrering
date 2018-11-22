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
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Slf4j
public class SykmeldtInfoClient extends BaseClient {

    public static final String DIGISYFO_BASE_URL_PROPERTY_NAME = "SYKEFRAVAERAPI_URL";
    public static final String API_KEY_FASIT_KEY = "VEILARBREGISTRERING_SYKEFRAVAERAPI_APIKEY_PASSWORD";
    private static String apiKey = "";

    @Inject
    public SykmeldtInfoClient(Provider<HttpServletRequest> httpServletRequestProvider) {
        super(getRequiredProperty(DIGISYFO_BASE_URL_PROPERTY_NAME), httpServletRequestProvider);
        this.apiKey = getRequiredProperty(API_KEY_FASIT_KEY);
    }

    public SykmeldtInfoData hentSykeforloepMetadata() {
        String cookies = httpServletRequestProvider.get().getHeader(COOKIE);
        return getSykeforloepMetadata(baseUrl + "sykeforloep/metadata" , cookies, SykmeldtInfoData.class);
    }

    private static <T> T getSykeforloepMetadata(String url, String cookies, Class<T> returnType) {
        try {
            return withClient(RestUtils.RestConfig.builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                        c -> c.target(url)
                                .request()
                                .header(COOKIE, cookies)
                                .header("x-nav-apiKey", apiKey)
                                .get(returnType));
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }
}