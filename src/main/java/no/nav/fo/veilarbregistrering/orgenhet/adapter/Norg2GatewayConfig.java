package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class Norg2GatewayConfig {

    private static final String NORG2_PROPERTY_NAME = "NORG2_URL";

    @Bean
    public Norg2RestClient norgRestClient() {
        return new Norg2RestClient(getRequiredProperty(NORG2_PROPERTY_NAME));
    }

    @Bean
    public Norg2Gateway norgGateway(Norg2RestClient norg2RestClient) {
        return new Norg2GatewayImpl(norg2RestClient);
    }
}
