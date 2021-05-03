package no.nav.fo.veilarbregistrering.config;

import no.nav.common.sts.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import static org.mockito.Mockito.mock;

@Configuration
public class ApplicationTestConfig extends ApplicationConfig {

    @Bean
    UserService userServiceStub() {
        return new StubUserService();
    }

    @Bean
    SystemUserTokenProvider systemUserTokenProvider() {
        return mock(SystemUserTokenProvider.class);
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
