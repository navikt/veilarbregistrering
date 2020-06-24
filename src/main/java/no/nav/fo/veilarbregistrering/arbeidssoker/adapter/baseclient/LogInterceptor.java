package no.nav.fo.veilarbregistrering.arbeidssoker.adapter.baseclient;

import no.nav.log.LogFilter;
import no.nav.log.MDCConstants;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.sbl.util.StringUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static no.nav.log.LogFilter.NAV_CALL_ID_HEADER_NAMES;

public class LogInterceptor implements Interceptor {

    private static final Logger LOG = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder();

        StringUtils.of(MDC.get(MDCConstants.MDC_CALL_ID))
                .ifPresent(callId -> stream(NAV_CALL_ID_HEADER_NAMES)
                        .forEach(headerName-> requestBuilder.header(headerName, callId)));

        EnvironmentUtils.getApplicationName().ifPresent(applicationName -> requestBuilder.header(LogFilter.CONSUMER_ID_HEADER_NAME, applicationName));

        Request request = requestBuilder
                .method(original.method(), original.body())
                .build();

        try {
            Response response = chain.proceed(request);
            LOG.info(format("%d %s %s", response.code(), request.method(), request.url()));
            return response;
        } catch (Exception exception) {
            LOG.error(format("Request failed: %s %s", request.method(), request.url()), exception);
            throw exception;
        }
    }

}
