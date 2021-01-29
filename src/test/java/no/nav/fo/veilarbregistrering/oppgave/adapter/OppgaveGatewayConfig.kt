package no.nav.fo.veilarbregistrering.oppgave.adapter

import no.nav.fo.veilarbregistrering.oppgave.Oppgave
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OppgaveGatewayConfig {
    @Bean
    open fun oppgaveGateway(): OppgaveGateway {
        return OppgaveGateway { _: Oppgave? -> null }
    }
}