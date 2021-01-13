package no.nav.fo.veilarbregistrering.arbeidsforhold;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdTestdataBuilder.*;

public class FlereArbeidsforholdTestdataBuilder {

    public static FlereArbeidsforhold flereArbeidsforholdTilfeldigSortert() {

        // Skal hente sistearbeidsforhold
        List<Arbeidsforhold> tilfeldigSortertListe = asList(eldre(), siste(), nestSiste());
        return FlereArbeidsforhold.of(tilfeldigSortertListe);
    }

    public static FlereArbeidsforhold somJson() {

        // Skal hente sistearbeidsforhold
        List<Arbeidsforhold> tilfeldigSortertListe = asList(new ArbeidsforholdTestdataBuilder()
                .organisasjonsnummer("981129687")
                .styrk("2130123")
                .periode(LocalDate.of(2014, 7, 1), LocalDate.of(2015, 12, 31))
                .navArbeidsforholdId("123456")
                .build());

        return FlereArbeidsforhold.of(tilfeldigSortertListe);
    }
}
