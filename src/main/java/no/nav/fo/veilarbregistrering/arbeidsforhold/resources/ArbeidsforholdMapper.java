package no.nav.fo.veilarbregistrering.arbeidsforhold.resources;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;

class ArbeidsforholdMapper {

    private ArbeidsforholdMapper() {
    }

    static ArbeidsforholdDto map(Arbeidsforhold arbeidsforhold) {
        ArbeidsforholdDto arbeidsforholdDto = new ArbeidsforholdDto();
        arbeidsforholdDto.setArbeidsgiverOrgnummer(arbeidsforhold.getArbeidsgiverOrgnummer());
        arbeidsforholdDto.setStyrk(arbeidsforhold.getStyrk());
        arbeidsforholdDto.setFom(arbeidsforhold.getFom());
        arbeidsforholdDto.setTom(arbeidsforhold.getTom());

        return arbeidsforholdDto;
    }
}