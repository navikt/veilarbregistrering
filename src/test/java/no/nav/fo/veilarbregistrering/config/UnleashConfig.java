package no.nav.fo.veilarbregistrering.config;

import no.nav.common.featuretoggle.UnleashService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class UnleashConfig {

    @Bean
    public UnleashService unleashService() {
        return mock(UnleashService.class);
    }

}
