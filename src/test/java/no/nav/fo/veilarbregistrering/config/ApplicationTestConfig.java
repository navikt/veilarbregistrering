package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.common.oidc.SystemUserTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;
import static org.mockito.Mockito.mock;

@Configuration
public class ApplicationTestConfig extends ApplicationConfig {

    @Bean
    SystemUserTokenProvider systemUserTokenProvider() {
        return mock(SystemUserTokenProvider.class);
    }

    @Bean
    GammelSystemUserTokenProvider gammelSystemUserTokenProvider() {
        return mock(GammelSystemUserTokenProvider.class);
    }

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {

    }
}
