package no.nav.fo.veilarbregistrering.oppgave.adapter;

import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OppgaveGatewayConfig {

    @Bean
    OppgaveGateway oppgaveGateway() {

        return (aktoerId, enhetsnr, beskrivelse, fristFerdigstillelse, aktivDato) -> null;
    }
}
