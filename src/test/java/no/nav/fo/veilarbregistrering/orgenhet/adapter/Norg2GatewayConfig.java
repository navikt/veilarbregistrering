package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class Norg2GatewayConfig {

    @Bean
    public Norg2Gateway norgGateway() {
        return mock(Norg2Gateway.class);
    }
}
