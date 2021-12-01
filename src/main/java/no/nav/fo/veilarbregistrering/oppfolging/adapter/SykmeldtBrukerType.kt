package no.nav.fo.veilarbregistrering.oppfolging.adapter

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.besvarelse.FremtidigSituasjonSvar
import no.nav.fo.veilarbregistrering.besvarelse.FremtidigSituasjonSvar.*

enum class SykmeldtBrukerType {
    SKAL_TIL_NY_ARBEIDSGIVER, SKAL_TIL_SAMME_ARBEIDSGIVER;

    companion object {
        private val tilSammeArbeidsgiverStatuser = listOf(
            SAMME_ARBEIDSGIVER,
            SAMME_ARBEIDSGIVER_NY_STILLING,
            INGEN_PASSER
        )
        fun of(besvarelse: Besvarelse): SykmeldtBrukerType =
            when (besvarelse.fremtidigSituasjon) {
                in tilSammeArbeidsgiverStatuser -> SKAL_TIL_SAMME_ARBEIDSGIVER
                else -> SKAL_TIL_NY_ARBEIDSGIVER
            }
    }
}