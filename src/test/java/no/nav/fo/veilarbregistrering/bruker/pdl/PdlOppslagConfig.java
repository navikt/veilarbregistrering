package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PdlOppslagConfig {

    @Bean
    PdlOppslagClient pdlOppslagClient() {
        return new PdlOppslagClient(null, null, null) {
        };
    }

    @Bean
    PdlOppslagGateway pdlOppslagGateway(PdlOppslagClient pdlOppslagClient) {
        return new PdlOppslagGatewayImpl(pdlOppslagClient);
    }
}
