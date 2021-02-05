package no.nav.fo.veilarbregistrering.autorisasjon;

import no.nav.common.abac.Pep;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class PepConfig {

    @Bean
    public Pep pepClient() {
        return mock(Pep.class);
    }

}
