package no.nav.fo.veilarbregistrering.oppgave.adapter

import no.nav.fo.veilarbregistrering.oppgave.OppgaveResponse

internal data class OppgaveResponseDto(
    override val id: Long = 0,
    override val tildeltEnhetsnr: String? = null
) : OppgaveResponse