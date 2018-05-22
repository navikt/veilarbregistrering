package no.nav.fo.veilarbregistrering.httpclient;

import no.nav.fo.veilarbregistrering.domain.AktiverBrukerData;
import no.nav.fo.veilarbregistrering.domain.ArenaOppfolging;
import no.nav.fo.veilarbregistrering.domain.OppfolgingStatus;
import no.nav.sbl.rest.RestUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Entity;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class OppfolgingClient {

    public static final String VEILARBOPPFOLGINGAPI_URL_PROPERTY_NAME = "VEILARBOPPFOLGINGAPI_URL";

    private final String veilarboppfolgingTarget;
    private final SystemUserAuthorizationInterceptor systemUserAuthorizationInterceptor;
    private final Provider<HttpServletRequest> httpServletRequestProvider;

    @Inject
    public OppfolgingClient(Provider<HttpServletRequest> httpServletRequestProvider) {
        this(getRequiredProperty(VEILARBOPPFOLGINGAPI_URL_PROPERTY_NAME), new SystemUserAuthorizationInterceptor(), httpServletRequestProvider);
    }

    public OppfolgingClient(String veilarboppfolgingTarget, SystemUserAuthorizationInterceptor systemUserAuthorizationInterceptor, Provider<HttpServletRequest> httpServletRequestProvider ) {
        this.veilarboppfolgingTarget = veilarboppfolgingTarget;
        this.systemUserAuthorizationInterceptor = systemUserAuthorizationInterceptor;
        this.httpServletRequestProvider = httpServletRequestProvider;
    }


    public void aktiverBruker(AktiverBrukerData aktiverBrukerData) {
        RestUtils.withClient(c -> c.target(veilarboppfolgingTarget + "/api/oppfolging/aktiverbruker")
                .register(systemUserAuthorizationInterceptor)
                .request()
                .post(Entity.json(aktiverBrukerData), AktiverBrukerData.class));
    }

    public Optional<OppfolgingStatus> hentOppfolgingsstatus(String fnr) {
        HttpServletRequest httpServletRequest = httpServletRequestProvider.get();

        Optional<OppfolgingStatus> oppfolgingStatus =
                ofNullable(RestUtils.withClient(c -> c.target(veilarboppfolgingTarget + "/api/oppfolging")
                .queryParam("fnr", fnr)
                .request()
                .header(COOKIE, httpServletRequest.getHeader(COOKIE))
                .get(OppfolgingStatus.class)));


        if (oppfolgingStatus.isPresent()) {
            Optional<ArenaOppfolging> arenaOppfolging =
                    ofNullable(RestUtils.withClient(c -> c.target(veilarboppfolgingTarget + "/api/person/" + fnr + "/oppfoelgingsstatus")
                    .queryParam("fnr", fnr)
                    .request()
                    .header(COOKIE, httpServletRequest.getHeader(COOKIE))
                    .get(ArenaOppfolging.class)));

            oppfolgingStatus.get().setInaktiveringsdato(arenaOppfolging.map(a -> a.getInaktiveringsdato()).orElse(null));
        }
        return oppfolgingStatus;
    }

}