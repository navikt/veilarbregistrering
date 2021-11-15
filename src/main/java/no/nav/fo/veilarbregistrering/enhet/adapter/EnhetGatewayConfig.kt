package no.nav.fo.veilarbregistrering.enhet.adapter;

import no.nav.fo.veilarbregistrering.enhet.EnhetGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.common.utils.EnvironmentUtils.getRequiredProperty;

@Configuration
public class EnhetGatewayConfig {

    private static final String ENHET_PROPERTY_NAME = "ENHET_URL";

    @Bean
    EnhetRestClient enhetClient() {
        return new EnhetRestClient(getRequiredProperty(ENHET_PROPERTY_NAME));
    }

    @Bean
    EnhetGateway enhetGateway(EnhetRestClient enhetRestClient) {
        return new EnhetGatewayImpl(enhetRestClient);
    }
}
