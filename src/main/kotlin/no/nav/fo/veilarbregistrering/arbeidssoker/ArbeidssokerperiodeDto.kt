package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import java.time.LocalDateTime

data class ArbeidssokerperiodeDto(
    val id: Int,
    val foedselsnummer: Foedselsnummer,
    val fra: LocalDateTime,
    val til: LocalDateTime? = null
)
