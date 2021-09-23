package no.nav.fo.veilarbregistrering.arbeidsforhold.resources

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold
import java.time.LocalDate

data class ArbeidsforholdDto(val arbeidsgiverOrgnummer: String?, val styrk: String, val fom: LocalDate?, val tom: LocalDate?) {

    companion object {
        fun fra(arbeidsforhold: Arbeidsforhold) =
            ArbeidsforholdDto(
                arbeidsforhold.arbeidsgiverOrgnummer,
                arbeidsforhold.styrk,
                arbeidsforhold.fom,
                arbeidsforhold.tom
            )
    }
}