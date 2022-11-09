package no.nav.fo.veilarbregistrering.kafka.meldekort

import java.time.LocalDate

data class MeldekortPeriode(val periodeFra: LocalDate, val periodeTil: LocalDate)
