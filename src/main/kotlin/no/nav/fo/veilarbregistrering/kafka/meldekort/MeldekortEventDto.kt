package no.nav.fo.veilarbregistrering.kafka.meldekort

import java.time.LocalDate
import java.time.LocalDateTime

/*
{
  "fnr": "06049727907",
  "kontrollMeldekortRef": 2599531,
  "arbeidssokerNestePeriode": true,
  "periodeFra": "2022-10-24",
  "periodeTil": "2022-11-06",
  "kortType": "MANUELL_ARENA",
  "opprettet": "2022-11-09T12:30:52.107"
}
 */

data class MeldekortEventDto(
    val fnr: String,
    val kontrollMeldekortRef: Long,
    val arbeidssokerNestePeriode: Boolean,
    val periodeFra: LocalDate,
    val periodeTil: LocalDate,
    val kortType: String,
    val opprettet: LocalDateTime
)