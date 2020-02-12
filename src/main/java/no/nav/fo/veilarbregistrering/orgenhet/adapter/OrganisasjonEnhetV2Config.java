package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import no.nav.fo.veilarbregistrering.orgenhet.HentEnheterGateway;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.OrganisasjonEnhetV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class OrganisasjonEnhetV2Config {

    public static final String NORG2_ORGANISASJONENHET_V2_URL = "VIRKSOMHET_ORGANISASJONENHET_V2_ENDPOINTURL";

    @Bean
    HentEnheterGateway hentEnheterService(OrganisasjonEnhetV2 organisasjonEnhetV2) {
        return new HentEnheterGatewayImpl(organisasjonEnhetV2);
    }

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
