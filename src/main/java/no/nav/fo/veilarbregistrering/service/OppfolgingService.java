package no.nav.fo.veilarbregistrering.service;


import no.nav.fo.veilarbregistrering.domain.AktiverArbeidssokerData;
import no.nav.fo.veilarbregistrering.domain.OppfolgingStatus;
import no.nav.sbl.rest.RestUtils;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import java.util.Optional;
import java.util.function.Function;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Component
public class OppfolgingService {

    public static final String VEILARBOPPFOLGINGAPI_URL_PROPERTY_NAME = "VEILARBOPPFOLGINGAPI_URL";
    private final String veilarboppfolgingTarget;
    private final SystemUserAuthorizationInterceptor systemUserAuthorizationInterceptor;

    public OppfolgingService() {
        this.veilarboppfolgingTarget = getRequiredProperty(VEILARBOPPFOLGINGAPI_URL_PROPERTY_NAME);
        this.systemUserAuthorizationInterceptor = new SystemUserAuthorizationInterceptor();
    }

    public void aktiverBruker(AktiverArbeidssokerData aktiverArbeidssokerData) {
        withClient(c -> c.target(veilarboppfolgingTarget + "/aktiverBruker")
                .request()
                .post(Entity.json(aktiverArbeidssokerData))
        );
    }

    public Optional<OppfolgingStatus> hentOppfolgingsstatusOgFlagg(String fnr) {
        return Optional.empty();
    }

    <T> T withClient(Function<Client, T> function) {
        return RestUtils.withClient(c -> {
            c.register(systemUserAuthorizationInterceptor);
            return function.apply(c);
        });
    }
}
