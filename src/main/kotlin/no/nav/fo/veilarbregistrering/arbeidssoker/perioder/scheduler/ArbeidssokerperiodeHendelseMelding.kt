package no.nav.fo.veilarbregistrering.arbeidssoker.perioder.scheduler

import java.time.LocalDateTime

data class ArbeidssokerperiodeHendelseMelding(
    val hendelse: Hendelse,
    val foedselsnummer: String,
    val tidspunkt: LocalDateTime,
)

enum class Hendelse {
    STARTET,
    STOPPET,
}
