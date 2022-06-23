package no.nav.fo.veilarbregistrering.registrering.gjelderfra

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import java.time.LocalDate
import java.time.LocalDateTime

data class GjelderFraDato(
    val id: Long,
    val foedselsnummer: Foedselsnummer,
    val dato: LocalDate,
    val brukerRegistreringId: Long,
    val opprettetDato: LocalDateTime
    )
