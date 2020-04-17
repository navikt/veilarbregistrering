package no.nav.fo.veilarbregistrering.oppgave.adapter;

import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.httpclient.BaseClient;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import static javax.ws.rs.client.Entity.json;
import static no.nav.sbl.rest.RestUtils.RestConfig.builder;
import static no.nav.sbl.rest.RestUtils.withClient;

public class OppgaveRestClient extends BaseClient {

    private SystemUserTokenProvider systemUserTokenProvider;

    public OppgaveRestClient(String baseUrl, Provider<HttpServletRequest> httpServletRequestProvider, SystemUserTokenProvider systemUserTokenProvider) {
        super(baseUrl, httpServletRequestProvider);
        this.systemUserTokenProvider = systemUserTokenProvider;
    }

    protected OppgaveResponseDto opprettOppgave(OppgaveDto oppgaveDto) {
        return withClient(
                builder().readTimeout(HTTP_READ_TIMEOUT).build()
                , c -> postOppgave(oppgaveDto, c)
        );
    }

    private OppgaveResponseDto postOppgave(OppgaveDto oppgaveDto, Client client) {
        String url = baseUrl + "/oppgaver";
        Response response = buildSystemAuthorizationRequestWithUrl(client, url)
                .post(json(oppgaveDto));

        Response.Status status = Response.Status.fromStatusCode(response.getStatus());

        if (status.equals(Response.Status.CREATED)) {
            return response.readEntity(OppgaveResponseDto.class);
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
