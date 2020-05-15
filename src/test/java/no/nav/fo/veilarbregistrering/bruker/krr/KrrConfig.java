package no.nav.fo.veilarbregistrering.bruker.krr;

import no.nav.fo.veilarbregistrering.bruker.KrrGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class KrrConfig {

    @Bean
    KrrGateway krrGateway() {
        return mock(KrrGateway.class);
    }
}
