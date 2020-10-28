package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class PepConfig {

    @Bean
    public VeilarbAbacPepClient pepClient() {
        return mock(VeilarbAbacPepClient.class);
    }

}
