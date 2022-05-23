package no.nav.fo.veilarbregistrering.oppfolging.adapter

import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe

data class AktiverBrukerData(val fnr: Fnr, val innsatsgruppe: Innsatsgruppe)

data class AktiverBrukerFeilDto(
    val id: String? = null,
    val type: ArenaFeilType? = null,
    val detaljer: DetaljerDto? = null,
)

data class DetaljerDto(
    val detaljertType: String? = null,
    val feilMelding: String? = null,
    val stackTrace: String? = null,
)

enum class ArenaFeilType {
    BRUKER_ER_UKJENT, BRUKER_KAN_IKKE_REAKTIVERES, BRUKER_KAN_IKKE_REAKTIVERES_FORENKLET, BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET, BRUKER_MANGLER_ARBEIDSTILLATELSE
}