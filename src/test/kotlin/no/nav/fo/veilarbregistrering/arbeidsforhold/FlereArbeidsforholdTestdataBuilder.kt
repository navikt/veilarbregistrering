package no.nav.fo.veilarbregistrering.arbeidsforhold

import java.time.LocalDate

object FlereArbeidsforholdTestdataBuilder {
    fun flereArbeidsforholdTilfeldigSortert(): FlereArbeidsforhold {

        // Skal hente sistearbeidsforhold
        val tilfeldigSortertListe = listOf(
            ArbeidsforholdTestdata.eldre(),
            ArbeidsforholdTestdata.siste(),
            ArbeidsforholdTestdata.nestSiste()
        )
        return FlereArbeidsforhold(tilfeldigSortertListe)
    }

    fun somJson(): FlereArbeidsforhold = FlereArbeidsforhold(
        listOf(
            Arbeidsforhold(
                arbeidsgiverOrgnummer = "981129687",
                styrk = "2130123",
                fom = LocalDate.of(2014, 7, 1),
                tom = LocalDate.of(2015, 12, 31),
                navArbeidsforholdId = "123456",
            )
        )
    )
}
