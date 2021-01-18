package no.nav.fo.veilarbregistrering.arbeidssoker.adapter.baseclient;


import no.nav.common.json.JsonUtils;
import no.nav.common.rest.client.RestClient;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class RestUtils {

    private static final Logger LOG = LoggerFactory.getLogger(RestUtils.class);

    public static MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    public static void throwIfNotSuccessful(Response response) {
        if (!response.isSuccessful()) {
            String message = String.format("Uventet status %d ved kall mot mot %s", response.code(), response.request().url().toString());
            LOG.error(message);
            throw new RuntimeException(message);
        }
    }

    public static Optional<String> getBodyStr(Response response) throws IOException {
        ResponseBody body = response.body();

        if (body == null) {
            return Optional.empty();
        }

        return Optional.of(body.string());
    }

    public static <T> Optional<T> parseJsonResponse(Response response, Class<T> classOfT) throws IOException {
        return getBodyStr(response).map(bodyStr -> JsonUtils.fromJson(bodyStr, classOfT));
    }

    public static <T> T parseJsonResponseOrThrow(Response response, Class<T> classOfT) throws IOException {
        return parseJsonResponse(response, classOfT)
                .orElseThrow(() -> new IllegalStateException("Unable to parse JSON object from response body"));
    }

    public static Response.Builder dummyResponseBuilder() {
        return new Response.Builder()
                .request(new Request.Builder().url("https://nav.no").build())
                .protocol(Protocol.HTTP_1_1)
                .message("");
    }



}
