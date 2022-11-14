package no.nav.fo.veilarbregistrering.kafka.meldekort

import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortPeriode
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.Meldekorttype
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
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
) {
    fun map(): MeldekortEvent {
        return MeldekortEvent(
            fnr = Foedselsnummer(fnr),
            erArbeidssokerNestePeriode = arbeidssokerNestePeriode,
            nåværendePeriode = MeldekortPeriode(periodeFra, periodeTil),
            meldekorttype = Meldekorttype.from(kortType),
            meldekortEventId = kontrollMeldekortRef,
            eventOpprettet = opprettet
        )
    }
}

