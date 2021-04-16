package no.nav.fo.veilarbregistrering.config;

import no.nav.common.featuretoggle.UnleashClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class UnleashConfig {

    @Bean
    public UnleashClient unleashClient() {
        return mock(UnleashClient.class);
    }

}
