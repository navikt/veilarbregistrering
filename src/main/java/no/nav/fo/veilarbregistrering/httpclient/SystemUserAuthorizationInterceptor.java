package no.nav.fo.veilarbregistrering.httpclient;



import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

public class SystemUserAuthorizationInterceptor implements ClientRequestFilter {

    private final SystemUserTokenProvider systemUserTokenProvider ;

    public SystemUserAuthorizationInterceptor() {
        this(new SystemUserTokenProvider());
    }

    public SystemUserAuthorizationInterceptor(SystemUserTokenProvider systemUserTokenProvider) {
        this.systemUserTokenProvider = systemUserTokenProvider;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().putSingle("Authorization", "Bearer " + systemUserTokenProvider.getToken());
    }

}
