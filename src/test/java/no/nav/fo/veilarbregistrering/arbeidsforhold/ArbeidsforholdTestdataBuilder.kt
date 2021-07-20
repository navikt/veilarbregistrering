package no.nav.fo.veilarbregistrering.arbeidsforhold

import java.time.LocalDate

class ArbeidsforholdTestdataBuilder {
    private var fom: LocalDate? = null
    private var tom: LocalDate? = null
    private var organisasjonsnummer: String? = null
    private var styrk: String? = null
    private var navArbeidsforholdId: String? = null

    fun periode(fom: LocalDate?, tom: LocalDate?): ArbeidsforholdTestdataBuilder {
        this.fom = fom
        this.tom = tom
        return this
    }

    fun organisasjonsnummer(organisasjonsnummer: String?): ArbeidsforholdTestdataBuilder {
        this.organisasjonsnummer = organisasjonsnummer
        return this
    }

    fun styrk(styrk: String?): ArbeidsforholdTestdataBuilder {
        this.styrk = styrk
        return this
    }

    fun navArbeidsforholdId(navArbeidsforholdId: String?): ArbeidsforholdTestdataBuilder {
        this.navArbeidsforholdId = navArbeidsforholdId
        return this
    }

    fun build(): Arbeidsforhold {
        return Arbeidsforhold(organisasjonsnummer, styrk, fom, tom, navArbeidsforholdId)
    }

    companion object {
        fun tidligereArbeidsforhold(): Arbeidsforhold {
            return ArbeidsforholdTestdataBuilder()
                .periode(
                    LocalDate.of(2012, 3, 1),
                    LocalDate.of(2016, 3, 31)
                )
                .build()
        }

        fun åpentArbeidsforhold(): Arbeidsforhold {
            return ArbeidsforholdTestdataBuilder()
                .periode(
                    LocalDate.of(2016, 4, 1),
                    null
                )
                .build()
        }

        fun paagaaende(): Arbeidsforhold {
            return ArbeidsforholdTestdataBuilder()
                .organisasjonsnummer("555555555")
                .periode(LocalDate.of(2017, 11, 1), null)
                .build()
        }

        fun siste(): Arbeidsforhold {
            return ArbeidsforholdTestdataBuilder()
                .organisasjonsnummer("123456789")
                .periode(
                    LocalDate.of(2017, 11, 1),
                    LocalDate.of(2017, 11, 30)
                )
                .build()
        }

        fun nestSiste(): Arbeidsforhold {
            return ArbeidsforholdTestdataBuilder()
                .organisasjonsnummer("987654321")
                .periode(
                    LocalDate.of(2017, 9, 1),
                    LocalDate.of(2017, 9, 30)
                )
                .build()
        }

        fun eldre(): Arbeidsforhold {
            return ArbeidsforholdTestdataBuilder()
                .periode(
                    LocalDate.of(2017, 4, 1),
                    LocalDate.of(2017, 4, 30)
                )
                .build()
        }

        @JvmStatic
        fun medDato(fom: LocalDate?, tom: LocalDate?): Arbeidsforhold {
            return ArbeidsforholdTestdataBuilder().periode(fom, tom).build()
        }
    }
}
