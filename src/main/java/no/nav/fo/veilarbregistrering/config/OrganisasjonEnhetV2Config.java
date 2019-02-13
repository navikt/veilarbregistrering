package no.nav.fo.veilarbregistrering.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.sbl.dialogarena.types.Pingable.Ping.PingMetadata;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.binding.OrganisasjonEnhetV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;
import static no.nav.sbl.dialogarena.types.Pingable.Ping;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class OrganisasjonEnhetV2Config {

    public static final String NORG2_ORGANISASJONENHET_V2_URL = "VIRKSOMHET_ORGANISASJONENHET_V2_ENDPOINTURL";

    @Bean
    public OrganisasjonEnhetV2 organisasjonEnhetV2() {
        return createTimerProxyForWebService("OrganisasjonEnhetV2", new CXFClient<>(OrganisasjonEnhetV2.class)
                .address(resolveOrganisasjonEnhetUrl())
                .configureStsForSystemUserInFSS()
                .build(), OrganisasjonEnhetV2.class);
    }

    private String resolveOrganisasjonEnhetUrl() {
        return getRequiredProperty(NORG2_ORGANISASJONENHET_V2_URL);
    }

}
