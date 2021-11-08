package no.nav.fo.veilarbregistrering.oppgave.adapter

import io.mockk.mockk
import no.nav.fo.veilarbregistrering.oppgave.Oppgave
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway
import no.nav.fo.veilarbregistrering.oppgave.OppgaveResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OppgaveGatewayConfig {
    @Bean
    fun oppgaveGateway(): OppgaveGateway =
        object : OppgaveGateway {
            override fun opprett(oppgave: Oppgave): OppgaveResponse = mockk()
        }

}