package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdTestdataBuilder;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdTestdataBuilder.*;

public class StubArbeidsforholdGateway extends SoapArbeidsforholdGateway {

    public StubArbeidsforholdGateway() {
        super(null);
    }

    @Override
    public FlereArbeidsforhold hentArbeidsforhold(Foedselsnummer fnr) {

        List<Arbeidsforhold> arbeidsforholdListe = new ArrayList();
        arbeidsforholdListe.add(tidligereArbeidsforhold());
        arbeidsforholdListe.add(åpentArbeidsforhold());

        return FlereArbeidsforhold.of(arbeidsforholdListe);
    }
}
