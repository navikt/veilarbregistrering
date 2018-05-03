package no.nav.fo.veilarbregistrering.mock;

import no.nav.fo.veilarbregistrering.domain.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.service.ArbeidsforholdService;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDate.now;

public class ArbeidsforholdServiceMock extends ArbeidsforholdService {

    public ArbeidsforholdServiceMock() {
        super(null);
    }

    @Override
    public List<Arbeidsforhold> hentArbeidsforhold(String fnr) {
        List arbeidsforholdListe = new ArrayList();
        arbeidsforholdListe.add(createArbeidsforhold(now().minusDays(200), now().minusDays(30)));
        arbeidsforholdListe.add(createArbeidsforhold(now().minusDays(30), null));

        return arbeidsforholdListe;
    }

    @Override
    public Arbeidsforhold hentSisteArbeidsforhold(String fnr) {
        return createArbeidsforhold(now().minusDays(30), null);
    }

    private Arbeidsforhold createArbeidsforhold(LocalDate from, LocalDate to) {
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold();
        arbeidsforhold.setStyrk("1234");
        arbeidsforhold.setFom(from);
        arbeidsforhold.setTom(to);
        return arbeidsforhold;
    }
}
