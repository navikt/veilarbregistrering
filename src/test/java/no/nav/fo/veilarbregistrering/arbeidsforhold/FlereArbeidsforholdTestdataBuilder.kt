package no.nav.fo.veilarbregistrering.arbeidsforhold

import java.time.LocalDate

object FlereArbeidsforholdTestdataBuilder {
    @JvmStatic
    fun flereArbeidsforholdTilfeldigSortert(): FlereArbeidsforhold {

        // Skal hente sistearbeidsforhold
        val tilfeldigSortertListe = listOf(
            ArbeidsforholdTestdataBuilder.eldre(),
            ArbeidsforholdTestdataBuilder.siste(),
            ArbeidsforholdTestdataBuilder.nestSiste()
        )
        return FlereArbeidsforhold.of(tilfeldigSortertListe)
    }

    @JvmStatic
    fun somJson(): FlereArbeidsforhold {

        // Skal hente sistearbeidsforhold
        val tilfeldigSortertListe = listOf(
            ArbeidsforholdTestdataBuilder()
                .organisasjonsnummer("981129687")
                .styrk("2130123")
                .periode(LocalDate.of(2014, 7, 1), LocalDate.of(2015, 12, 31))
                .navArbeidsforholdId("123456")
                .build()
        )
        return FlereArbeidsforhold.of(tilfeldigSortertListe)
    }
}
