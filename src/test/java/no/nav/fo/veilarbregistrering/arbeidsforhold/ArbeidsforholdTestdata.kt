package no.nav.fo.veilarbregistrering.arbeidsforhold

import java.time.LocalDate
object ArbeidsforholdTestdata {
    fun paagaaende(): Arbeidsforhold =
        Arbeidsforhold(
            arbeidsgiverOrgnummer = "555555555",
            fom = LocalDate.of(2017, 11, 1),
            tom = null,
            navArbeidsforholdId = "123456"
        )

    fun siste(): Arbeidsforhold =
        Arbeidsforhold(
            arbeidsgiverOrgnummer = "123456789",
            fom = LocalDate.of(2017, 11, 1),
            tom = LocalDate.of(2017, 11, 30),
            navArbeidsforholdId = "123456"
        )

    fun nestSiste(): Arbeidsforhold =
        Arbeidsforhold(
            arbeidsgiverOrgnummer = "987654321",
            fom = LocalDate.of(2017, 9, 1),
            tom = LocalDate.of(2017, 9, 30),
            navArbeidsforholdId = "123456"
        )

    fun eldre(): Arbeidsforhold =
        Arbeidsforhold(
            arbeidsgiverOrgnummer = null,
            fom = LocalDate.of(2017, 4, 1),
            tom = LocalDate.of(2017, 4, 30),
            navArbeidsforholdId = "123456"
        )

    fun medDato(fom: LocalDate?, tom: LocalDate?): Arbeidsforhold =
        Arbeidsforhold(
            arbeidsgiverOrgnummer = null,
            fom = fom,
            tom = tom,
            navArbeidsforholdId = "123456"
        )
}


