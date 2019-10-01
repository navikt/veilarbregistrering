package no.nav.fo.veilarbregistrering.arbeidsforhold;

import java.util.List;

public interface ArbeidsforholdGateway {

    List<Arbeidsforhold> hentArbeidsforhold(String fnr);

}
