package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukersTilstand

object StartRegistreringStatusDtoMapper {
    @JvmStatic
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
        brukersTilstand.isHarIgangsattGjenopptagbarRegistrering,
        brukersTilstand.formidlingsgruppe?.kode,
        brukersTilstand.servicegruppe?.servicegruppe,
        brukersTilstand.rettighetsgruppe?.stringValue(),
        geografiskTilknytning?.stringValue(),
        alder
    )
}