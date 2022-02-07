package no.nav.fo.veilarbregistrering.oppgave

interface OppgaveGateway {
    fun opprett(oppgave: Oppgave): OppgaveResponse
}