package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistrering;
import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.FremtidigSituasjonSvar;

public enum SykmeldtBrukerType {
    SKAL_TIL_NY_ARBEIDSGIVER,
    SKAL_TIL_SAMME_ARBEIDSGIVER;

    static SykmeldtBrukerType of(SykmeldtRegistrering sykmeldtRegistrering) {
        FremtidigSituasjonSvar fremtidigSituasjon = sykmeldtRegistrering.getBesvarelse().getFremtidigSituasjon();
        if  (fremtidigSituasjon == FremtidigSituasjonSvar.SAMME_ARBEIDSGIVER
                || fremtidigSituasjon == FremtidigSituasjonSvar.SAMME_ARBEIDSGIVER_NY_STILLING
                || fremtidigSituasjon == FremtidigSituasjonSvar.INGEN_PASSER
        ) {
            return SKAL_TIL_SAMME_ARBEIDSGIVER;
        } else {
            return SKAL_TIL_NY_ARBEIDSGIVER;
        }

    }
}
