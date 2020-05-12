package no.nav.fo.veilarbregistrering.enhet.adapter;

import no.nav.fo.veilarbregistrering.enhet.EnhetGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class EnhetGatewayConfig {

    public static final String ENHET_PROPERTY_NAME = "ENHET_URL";

    @Bean
    EnhetClient enhetClient() {
        return new EnhetClient(getRequiredProperty(ENHET_PROPERTY_NAME));
    }

    @Bean
    EnhetGateway enhetGateway(EnhetClient enhetClient) {
        return new EnhetGatewayImpl(enhetClient);
    }
}
