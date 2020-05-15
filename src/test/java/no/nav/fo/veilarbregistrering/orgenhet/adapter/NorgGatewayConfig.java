package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import no.nav.fo.veilarbregistrering.orgenhet.NorgGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class NorgGatewayConfig {

    @Bean
    public NorgGateway norgGateway() {
        return mock(NorgGateway.class);
    }
}
