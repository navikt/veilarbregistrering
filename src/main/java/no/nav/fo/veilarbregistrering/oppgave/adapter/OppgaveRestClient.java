package no.nav.fo.veilarbregistrering.oppgave.adapter;

import no.nav.common.rest.client.RestClient;
import no.nav.common.rest.client.RestUtils;
import no.nav.common.sts.SystemUserTokenProvider;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OppgaveRestClient {

    private static final int HTTP_READ_TIMEOUT = 120000;

    private final String baseUrl;
    private final OkHttpClient client;
    private SystemUserTokenProvider systemUserTokenProvider;

    public OppgaveRestClient(String baseUrl, SystemUserTokenProvider systemUserTokenProvider) {
        this.baseUrl = baseUrl;
        this.systemUserTokenProvider = systemUserTokenProvider;
        this.client = RestClient.baseClientBuilder()
                .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS).build();
    }

    protected OppgaveResponseDto opprettOppgave(OppgaveDto oppgaveDto) {
        String url = baseUrl + "/oppgaver";

        try {
            Response response = client.newCall(
                    buildSystemAuthorizationRequestWithUrl(url)
                            .method("post", RestUtils.toJsonRequestBody(oppgaveDto))
                            .build()
            ).execute();

            if (response.code() == HttpStatus.CREATED.value()) {
                return RestUtils.parseJsonResponseOrThrow(response, OppgaveResponseDto.class);
            }
            throw new RuntimeException("Opprett oppgave feilet med statuskode: " + response.code() + " - " + response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Request.Builder buildSystemAuthorizationRequestWithUrl(String url) {
        return new Request.Builder()
                .url(url)
                .header("Authorization",
                        "Bearer " + this.systemUserTokenProvider.getSystemUserToken());
    }
}
