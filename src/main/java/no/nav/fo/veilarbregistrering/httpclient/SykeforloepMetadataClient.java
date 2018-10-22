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
public class SykeforloepMetadataClient extends BaseClient {

    public static final String SYKEFORLOEPMETADATA_URL_PROPERTY_NAME = "SYKEFORLOEPMETADATA_URL";

    @Inject
    public SykeforloepMetadataClient(Provider<HttpServletRequest> httpServletRequestProvider) {
        super(getRequiredProperty(SYKEFORLOEPMETADATA_URL_PROPERTY_NAME), httpServletRequestProvider);
    }

    public SykeforloepMetaData hentSykeforloepMetadata() {
        String cookies = httpServletRequestProvider.get().getHeader(COOKIE);
        return getSykeforloepMetadata(baseUrl , cookies, SykeforloepMetaData.class);
    }

    private static <T> T getSykeforloepMetadata(String url, String cookies, Class<T> returnType) {
        return Try.of(() ->
                withClient(RestUtils.RestConfig.builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                        c -> c.target(url).request().header(COOKIE, cookies).get(returnType)))
                .onFailure((e) -> {
                    log.error("Feil ved kall til Sykeforloep metadata {}", url, e);
                    throw new InternalServerErrorException();
                })
                .get();
    }
}