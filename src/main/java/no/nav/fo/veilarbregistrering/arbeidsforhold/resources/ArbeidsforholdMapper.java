package no.nav.fo.veilarbregistrering.arbeidsforhold.resources;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;

class ArbeidsforholdMapper {

    private ArbeidsforholdMapper() {
    }

    static ArbeidsforholdDto map(Arbeidsforhold arbeidsforhold) {
        return new ArbeidsforholdDto(
                arbeidsforhold.getArbeidsgiverOrgnummer(),
                arbeidsforhold.getStyrk(),
                arbeidsforhold.getFom(),
                arbeidsforhold.getTom()
        );
    }
}