package no.nav.fo.veilarbregistrering.httpclient;

import no.nav.fo.veilarbregistrering.domain.AktiverBrukerData;
import no.nav.fo.veilarbregistrering.domain.ArenaOppfolging;
import no.nav.fo.veilarbregistrering.domain.OppfolgingStatus;
import no.nav.sbl.rest.RestUtils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.ofNullable;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class OppfolgingClient {

    public static final String VEILARBOPPFOLGINGAPI_URL_PROPERTY_NAME = "VEILARBOPPFOLGINGAPI_URL";

    private final String veilarboppfolgingTarget;
    private final SystemUserAuthorizationInterceptor systemUserAuthorizationInterceptor;

    public OppfolgingClient() {
        this(getRequiredProperty(VEILARBOPPFOLGINGAPI_URL_PROPERTY_NAME), new SystemUserAuthorizationInterceptor());
    }

    public OppfolgingClient(String veilarboppfolgingTarget, SystemUserAuthorizationInterceptor systemUserAuthorizationInterceptor) {
        this.veilarboppfolgingTarget = veilarboppfolgingTarget;
        this.systemUserAuthorizationInterceptor = systemUserAuthorizationInterceptor;
    }

    <T> T withClient(Function<Client, T> function) {
        return RestUtils.withClient(c -> {
            c.register(systemUserAuthorizationInterceptor);
            return function.apply(c);
        });
    }

    public void aktiverBruker(AktiverBrukerData aktiverBrukerData) {
        withClient(c -> c.target(veilarboppfolgingTarget + "/api/aktiverbruker")
                .request()
                .post(Entity.json(aktiverBrukerData), AktiverBrukerData.class));
    }

    public Optional<OppfolgingStatus> hentOppfolgingsstatus(String fnr) {
        Optional<OppfolgingStatus> oppfolgingStatus =
                ofNullable(withClient(c -> c.target(veilarboppfolgingTarget + "/api/oppfolging")
                .queryParam("fnr", fnr)
                .request()
                .get(OppfolgingStatus.class)));


        if (oppfolgingStatus.isPresent()) {
            Optional<ArenaOppfolging> arenaOppfolging =
                    ofNullable(withClient(c -> c.target(veilarboppfolgingTarget + "/api/person/" + fnr + "oppfoelgingsstatus")
                    .queryParam("fnr", fnr)
                    .request()
                    .get(ArenaOppfolging.class)));

            oppfolgingStatus.get().setInaktiveringsdato(arenaOppfolging.map(a -> a.getInaktiveringsdato()).orElse(null));
        }
        return oppfolgingStatus;
    }

}