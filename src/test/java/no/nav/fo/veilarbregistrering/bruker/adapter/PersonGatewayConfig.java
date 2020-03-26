package no.nav.fo.veilarbregistrering.bruker.adapter;

import no.nav.fo.veilarbregistrering.bruker.PersonGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class PersonGatewayConfig {

    @Bean
    PersonGateway personGateway() {
        return foedselsnummer -> Optional.empty();
    }
}
