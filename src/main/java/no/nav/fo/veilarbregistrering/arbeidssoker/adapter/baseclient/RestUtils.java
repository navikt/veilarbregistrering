package no.nav.fo.veilarbregistrering.arbeidssoker.adapter.baseclient;

import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

public class RestUtils {

    public static Response.Builder dummyResponseBuilder() {
        return new Response.Builder()
                .request(new Request.Builder().url("https://nav.no").build())
                .protocol(Protocol.HTTP_1_1)
                .message("");
    }
}
