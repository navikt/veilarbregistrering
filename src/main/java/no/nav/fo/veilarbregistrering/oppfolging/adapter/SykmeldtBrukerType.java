package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.besvarelse.FremtidigSituasjonSvar;

public enum SykmeldtBrukerType {
    SKAL_TIL_NY_ARBEIDSGIVER,
    SKAL_TIL_SAMME_ARBEIDSGIVER;

    static SykmeldtBrukerType of(Besvarelse besvarelse) {
        FremtidigSituasjonSvar fremtidigSituasjon = besvarelse.getFremtidigSituasjon();
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
