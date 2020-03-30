package no.nav.fo.veilarbregistrering.bruker.aktor;

import no.nav.fo.veilarbregistrering.bruker.AktorGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AktorConfig {

    @Bean
    public AktorGateway aktorGateway() {
        return new AktorGatewayImpl(new AktorServiceMock());
    }
}
