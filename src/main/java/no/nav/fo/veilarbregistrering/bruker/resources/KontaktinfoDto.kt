package no.nav.fo.veilarbregistrering.bruker.resources

import no.nav.fo.veilarbregistrering.bruker.Navn

data class KontaktinfoDto(
    val telefonnummerHosKrr: String? = null,
    val telefonnummerHosNav: String? = null,
    val navn: Navn? = null,
)