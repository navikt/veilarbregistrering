package no.nav.fo.veilarbregistrering.httpclient;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import no.nav.fo.veilarbregistrering.domain.SykeforloepMetaData;
import no.nav.sbl.rest.RestUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;

import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static no.nav.sbl.rest.RestUtils.withClient;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Slf4j
public class DigisyfoClient extends BaseClient {

    public static final String DIGISYFO_BASE_URL_PROPERTY_NAME = "SYKEFRAVAERAPI_URL";
    public static final String API_KEY_FASIT_KEY = "VEILARBREGISTRERING_SYKEFRAVAERAPI_APIKEY_PASSWORD";
    private static String apiKey = "";

    @Inject
    public DigisyfoClient(Provider<HttpServletRequest> httpServletRequestProvider) {
        super(getRequiredProperty(DIGISYFO_BASE_URL_PROPERTY_NAME), httpServletRequestProvider);
        this.apiKey = getRequiredProperty(API_KEY_FASIT_KEY);
    }

    public SykeforloepMetaData hentSykeforloepMetadata() {
        String cookies = httpServletRequestProvider.get().getHeader(COOKIE);
        return getSykeforloepMetadata(baseUrl + "sykeforloep/metadata" , cookies, SykeforloepMetaData.class);
    }

    private static <T> T getSykeforloepMetadata(String url, String cookies, Class<T> returnType) {
        return Try.of(() ->
                withClient(RestUtils.RestConfig.builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                        c -> c.target(url)
                                .request()
                                .header(COOKIE, cookies)
                                .header("x-nav-apiKey", apiKey)
                                .get(returnType)))
                .onFailure((e) -> {
                    log.error("Feil ved kall til Sykeforloep metadata {}", url, e);
                    throw new InternalServerErrorException();
                })
                .get();
    }
}