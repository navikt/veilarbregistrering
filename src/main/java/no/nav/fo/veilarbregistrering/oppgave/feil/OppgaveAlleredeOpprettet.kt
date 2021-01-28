package no.nav.fo.veilarbregistrering.oppgave.feil

class OppgaveAlleredeOpprettet(kode: String, melding: String) : RuntimeException(melding)