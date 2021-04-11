package no.nav.fo.veilarbregistrering.tidslinje.resources

import no.nav.fo.veilarbregistrering.tidslinje.Kilde
import no.nav.fo.veilarbregistrering.tidslinje.Status

data class HistoriskElementDto(val periode: PeriodeDto, val kilde: Kilde, val status: Status)