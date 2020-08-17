package no.nav.fo.veilarbregistrering.registrering.resources;

import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning;
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe;
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukersTilstand;

import java.util.Optional;

public class StartRegistreringStatusDtoMapper {

    public static StartRegistreringStatusDto map(
            BrukersTilstand brukersTilstand,
            Optional<GeografiskTilknytning> muligGeografiskTilknytning,
            Boolean oppfyllerBetingelseOmArbeidserfaring,
            int alder) {

        return new StartRegistreringStatusDto()
                .setUnderOppfolging(brukersTilstand.isUnderOppfolging())
                .setRegistreringType(brukersTilstand.getRegistreringstype())
                .setErSykmeldtMedArbeidsgiver(brukersTilstand.isErSykmeldtMedArbeidsgiver())
                .setMaksDato(brukersTilstand.getMaksDato())
                .setJobbetSeksAvTolvSisteManeder(oppfyllerBetingelseOmArbeidserfaring)
                .setFormidlingsgruppe(brukersTilstand.getFormidlingsgruppe().orElse(Formidlingsgruppe.nullable()).stringValue())
                .setServicegruppe(brukersTilstand.getServicegruppe().orElse(Servicegruppe.nullable()).stringValue())
                .setRettighetsgruppe(brukersTilstand.getRettighetsgruppe().orElse(Rettighetsgruppe.nullable()).stringValue())
                .setGeografiskTilknytning(muligGeografiskTilknytning.map(GeografiskTilknytning::stringValue).orElse(null))
                .setAlder(alder);
    }
}
