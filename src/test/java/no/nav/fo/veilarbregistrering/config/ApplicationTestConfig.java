package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;

@Configuration
public class ApplicationTestConfig extends ApplicationConfig {

    @Bean
    UserService userService() {
        return new StubUserService();
    }

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

    private class StubUserService extends UserService {

        public StubUserService() {
            super(null, null);
        }

        @Override
        public Bruker finnBrukerGjennomPdl() {
            return Bruker.of(FoedselsnummerTestdataBuilder.aremark(), AktorId.of("232SA"));
        }
    }
}
