package no.nav.fo.veilarbregistrering.enhet.adapter;

import no.nav.fo.veilarbregistrering.enhet.EnhetGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class EnhetGatewayConfig {

    @Bean
    EnhetGateway enhetGateway() {
        return mock(EnhetGateway.class);
    }
}
