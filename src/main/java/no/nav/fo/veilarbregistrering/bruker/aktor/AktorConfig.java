package no.nav.fo.veilarbregistrering.bruker.aktor;

import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.bruker.AktorGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(no.nav.dialogarena.aktor.AktorConfig.class)
public class AktorConfig {

    @Bean
    public AktorGateway aktorGateway(AktorService aktorService) {
        return new AktorGatewayImpl(aktorService);
    }
}
