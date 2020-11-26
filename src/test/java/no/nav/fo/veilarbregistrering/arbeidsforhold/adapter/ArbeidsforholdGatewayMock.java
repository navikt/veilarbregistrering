package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDate.now;

public class ArbeidsforholdGatewayMock extends ArbeidsforholdGatewayImpl {

    public ArbeidsforholdGatewayMock() {
        super(null);
    }

    @Override
    public FlereArbeidsforhold hentArbeidsforhold(Foedselsnummer fnr) {
        List<Arbeidsforhold> arbeidsforholdListe = new ArrayList();
        arbeidsforholdListe.add(createArbeidsforhold(now().minusDays(200), now().minusDays(30)));
        arbeidsforholdListe.add(createArbeidsforhold(now().minusDays(30), null));

        return FlereArbeidsforhold.of(arbeidsforholdListe);
    }

    private Arbeidsforhold createArbeidsforhold(LocalDate from, LocalDate to) {
        return new Arbeidsforhold(null, null, from, to);
    }
}
