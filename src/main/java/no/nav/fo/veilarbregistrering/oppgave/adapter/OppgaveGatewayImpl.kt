package no.nav.fo.veilarbregistrering.oppgave.adapter

import no.nav.fo.veilarbregistrering.oppgave.Oppgave
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway
import no.nav.fo.veilarbregistrering.oppgave.OppgaveResponse
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr

class OppgaveGatewayImpl(private val restClient: OppgaveRestClient) : OppgaveGateway {
    override fun opprett(oppgave: Oppgave): OppgaveResponse {
        val oppgaveDto = OppgaveDto(oppgave.aktorId.asString(),
            oppgave.beskrivelse,
            TEMA_OPPFOLGING,
            OPPGAVETYPE_KONTAKT_BRUKER,
            oppgave.fristFerdigstillelse.toString(),
            oppgave.aktivDato.toString(),
            PRIORITET_NORM,
            oppgave.enhetnr?.asString()
        )
        return restClient.opprettOppgave(oppgaveDto)
    }

    companion object {
        private const val OPPGAVETYPE_KONTAKT_BRUKER = "KONT_BRUK"
        private const val TEMA_OPPFOLGING = "OPP"
        private const val PRIORITET_NORM = "NORM"
    }
}