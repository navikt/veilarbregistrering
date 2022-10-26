package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukersTilstand

object StartRegistreringStatusDtoMapper {
    fun map(
        brukersTilstand: BrukersTilstand,
        geografiskTilknytning: GeografiskTilknytning?,
        oppfyllerBetingelseOmArbeidserfaring: Boolean?,
        alder: Int
    ): StartRegistreringStatusDto = StartRegistreringStatusDto(
        null,
        brukersTilstand.isUnderOppfolging,
        brukersTilstand.isErSykmeldtMedArbeidsgiver,
        oppfyllerBetingelseOmArbeidserfaring,
        brukersTilstand.registreringstype,
        false,
        brukersTilstand.formidlingsgruppe?.kode,
        brukersTilstand.servicegruppe?.kode,
        brukersTilstand.rettighetsgruppe?.kode,
        geografiskTilknytning?.stringValue(),
        alder
    )
}