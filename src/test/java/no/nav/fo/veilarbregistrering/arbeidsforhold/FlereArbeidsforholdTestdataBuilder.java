package no.nav.fo.veilarbregistrering.arbeidsforhold;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdTestdataBuilder.*;

public class FlereArbeidsforholdTestdataBuilder {

    public static FlereArbeidsforhold flereArbeidsforholdTilfeldigSortert() {

        // Skal hente sistearbeidsforhold
        List<Arbeidsforhold> tilfeldigSortertListe = asList(eldre(), siste(), nestSiste());
        return FlereArbeidsforhold.of(tilfeldigSortertListe);
    }
}
