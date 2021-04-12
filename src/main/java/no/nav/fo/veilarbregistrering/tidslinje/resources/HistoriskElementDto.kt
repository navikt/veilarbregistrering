package no.nav.fo.veilarbregistrering.tidslinje.resources

import no.nav.fo.veilarbregistrering.tidslinje.Kilde
import no.nav.fo.veilarbregistrering.tidslinje.Type

data class HistoriskElementDto(val periode: PeriodeDto, val kilde: Kilde, val type: Type)