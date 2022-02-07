package no.nav.fo.veilarbregistrering.oppgave.adapter

@Suppress("unused")
internal class OppgaveDto(
    val aktoerId: String,
    val beskrivelse: String,
    val tema: String,
    val oppgavetype: String,
    val fristFerdigstillelse: String,
    val aktivDato: String,
    val prioritet: String,
    val tildeltEnhetsnr: String?
)