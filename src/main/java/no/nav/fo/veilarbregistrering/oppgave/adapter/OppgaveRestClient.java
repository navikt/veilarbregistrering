package no.nav.fo.veilarbregistrering.oppgave.adapter;

import no.nav.common.health.HealthCheck;
import no.nav.common.health.HealthCheckResult;
import no.nav.common.health.HealthCheckUtils;
import no.nav.common.rest.client.RestClient;
import no.nav.common.rest.client.RestUtils;
import no.nav.common.sts.SystemUserTokenProvider;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OppgaveRestClient implements HealthCheck {

    private static final int HTTP_READ_TIMEOUT = 120000;

    private final String baseUrl;
    private final OkHttpClient client;
    private SystemUserTokenProvider systemUserTokenProvider;

    OppgaveRestClient(String baseUrl, SystemUserTokenProvider systemUserTokenProvider) {
        this.baseUrl = baseUrl;
        this.systemUserTokenProvider = systemUserTokenProvider;
        this.client = RestClient.baseClientBuilder()
                .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS).build();
    }

    OppgaveResponseDto opprettOppgave(OppgaveDto oppgaveDto) {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/v1/oppgaver")
                .header("Authorization", "Bearer " + systemUserTokenProvider.getSystemUserToken())
                .method("POST", RestUtils.toJsonRequestBody(oppgaveDto))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() != HttpStatus.CREATED.value()) {
                throw new RuntimeException("Opprett oppgave feilet med statuskode: " + response.code() + " - " + response);
            }

            return RestUtils.parseJsonResponseOrThrow(response, OppgaveResponseDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HealthCheckResult checkHealth() {
        return HealthCheckUtils.pingUrl(baseUrl, client);
    }
}