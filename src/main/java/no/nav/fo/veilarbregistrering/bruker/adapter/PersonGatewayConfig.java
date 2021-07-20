package no.nav.fo.veilarbregistrering.bruker.adapter;


import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PersonGatewayConfig {

    @Bean
    PersonGatewayImpl personGateway(PdlOppslagGateway pdlOppslagGateway) {
        return new PersonGatewayImpl(pdlOppslagGateway);
    }
}
