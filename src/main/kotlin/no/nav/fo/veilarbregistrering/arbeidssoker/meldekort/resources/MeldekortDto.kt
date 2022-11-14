package no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.resources

import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.Meldekorttype
import java.time.LocalDate
import java.time.LocalDateTime

data class MeldekortDto(
    val erArbeidssokerNestePeriode: Boolean,
    val periodeFra: LocalDate,
    val periodeTil: LocalDate,
    val meldekorttype: Meldekorttype,
    val eventOpprettet: LocalDateTime
) {
    companion object {
        fun map(meldekortEvent: MeldekortEvent) = MeldekortDto(
            meldekortEvent.erArbeidssokerNestePeriode,
            meldekortEvent.nåværendePeriode.periodeFra,
            meldekortEvent.nåværendePeriode.periodeTil,
            meldekortEvent.meldekorttype,
            meldekortEvent.eventOpprettet
        )
    }
}
