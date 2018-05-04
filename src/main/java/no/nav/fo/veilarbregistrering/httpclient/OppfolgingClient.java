package no.nav.fo.veilarbregistrering.httpclient;

import no.nav.fo.veilarbregistrering.domain.OppfolgingStatus;
import no.nav.sbl.rest.RestUtils;

import javax.ws.rs.client.Client;
import java.util.function.Function;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class OppfolgingClient {

    public static final String VEILARBOPPFOLGINGAPI_URL_PROPERTY_NAME = "VEILARBOPPFOLGINGAPI_URL";
    public static final String FNR_QUERY_PARAM = "fnr";

    private final String veilarboppfolgingTarget;
    private final SystemUserAuthorizationInterceptor systemUserAuthorizationInterceptor;

    @SuppressWarnings("unused")
    public OppfolgingClient() {
        this(getRequiredProperty(VEILARBOPPFOLGINGAPI_URL_PROPERTY_NAME), new SystemUserAuthorizationInterceptor());
    }

    OppfolgingClient(String veilarboppfolgingTarget, SystemUserAuthorizationInterceptor systemUserAuthorizationInterceptor) {
        this.veilarboppfolgingTarget = veilarboppfolgingTarget;
        this.systemUserAuthorizationInterceptor = systemUserAuthorizationInterceptor;
    }


    public boolean underOppfolging(String fnr) {
        OppfolgingStatus oppfolgingStatus = withClient(c -> c.target(veilarboppfolgingTarget + "/oppfolging")
                        .queryParam(FNR_QUERY_PARAM, fnr)
                        .request()
                        .get(OppfolgingStatus.class)
        );
        //
        // return oppfolgingStatus.isUnderOppfolging();
        return true;
    }

    <T> T withClient(Function<Client, T> function) {
        return RestUtils.withClient(c -> {
            c.register(systemUserAuthorizationInterceptor);
            return function.apply(c);
        });
    }
}