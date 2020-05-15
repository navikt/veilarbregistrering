package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import no.nav.fo.veilarbregistrering.orgenhet.NorgGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class NorgGatewayConfig {

    private static final String NORG2_PROPERTY_NAME = "NORG2_URL";

    @Bean
    public NorgRestClient norgRestClient() {
        return new NorgRestClient(getRequiredProperty(NORG2_PROPERTY_NAME));
    }

    @Bean
    public NorgGateway norgGateway(NorgRestClient norgRestClient) {
        return new NorgGatewayImpl(norgRestClient);
    }
}
