package no.nav.fo.veilarbregistrering.oppgave.adapter;

import no.nav.common.oidc.SystemUserTokenProvider;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import static javax.ws.rs.client.Entity.json;
import static no.nav.sbl.rest.RestUtils.RestConfig.builder;
import static no.nav.sbl.rest.RestUtils.withClient;

public class OppgaveRestClient {

    private static final int HTTP_READ_TIMEOUT = 120000;

    private final String baseUrl;
    private SystemUserTokenProvider systemUserTokenProvider;

    public OppgaveRestClient(String baseUrl, SystemUserTokenProvider systemUserTokenProvider) {
        this.baseUrl = baseUrl;
        this.systemUserTokenProvider = systemUserTokenProvider;
    }

    protected OppgaveResponseResponseDto opprettOppgave(OppgaveDto oppgaveDto) {
        return withClient(
                builder().readTimeout(HTTP_READ_TIMEOUT).build()
                , c -> postOppgave(oppgaveDto, c)
        );
    }

    private OppgaveResponseResponseDto postOppgave(OppgaveDto oppgaveDto, Client client) {
        String url = baseUrl + "/oppgaver";
        Response response = buildSystemAuthorizationRequestWithUrl(client, url)
                .post(json(oppgaveDto));

        Response.Status status = Response.Status.fromStatusCode(response.getStatus());

        if (status.equals(Response.Status.CREATED)) {
            return response.readEntity(OppgaveResponseResponseDto.class);
        }

        throw new RuntimeException("Opprett oppgave feilet med statuskode: " + status + " - " + response);
    }

    private Invocation.Builder buildSystemAuthorizationRequestWithUrl(Client client, String url) {
        return client.target(url)
                .request()
                .header("Authorization",
                        "Bearer " + this.systemUserTokenProvider.getSystemUserAccessToken());
    }
}
