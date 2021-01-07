package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;

import java.time.LocalDate;

import static java.util.Optional.ofNullable;

class ArbeidsforholdMapperV2 {
    static Arbeidsforhold map(ArbeidsforholdDto arbeidsforholdDto) {
        return new Arbeidsforhold(map(arbeidsforholdDto.getArbeidsgiver()),
                arbeidsforholdDto.getArbeidsavtaler().stream()
                        .findFirst()
                        .map(ArbeidsavtaleDto::getYrke)
                        .orElse("utenstyrkkode"),
                getFom(arbeidsforholdDto.getAnsettelsesperiode()),
                getTom(arbeidsforholdDto.getAnsettelsesperiode()),
                ofNullable(arbeidsforholdDto.getNavArbeidsforholdId())
                        .map(id -> id.toString())
                        .orElse(null));
    }

    private static String map(ArbeidsgiverDto arbeidsgiver) {
        if ("Organisasjon".equals(arbeidsgiver.getType())) {
            return arbeidsgiver.getOrganisasjonsnummer();
        }
        return null;
    }

    private static LocalDate getFom(AnsettelsesperiodeDto ansettelsesPeriode) {
        return ofNullable(ansettelsesPeriode)
                .map(AnsettelsesperiodeDto::getPeriode)
                .map(PeriodeDto::getFom)
                .map(ArbeidsforholdMapperV2::stringToLocalDate)
                .orElse(null);
    }

    private static LocalDate getTom(AnsettelsesperiodeDto periode) {
        return ofNullable(periode)
                .map(AnsettelsesperiodeDto::getPeriode)
                .map(PeriodeDto::getTom)
                .map(ArbeidsforholdMapperV2::stringToLocalDate).orElse(null);
    }

    private static LocalDate stringToLocalDate(String dato) {
        return ofNullable(dato)
                .map(d -> LocalDate.parse(dato))
                .orElse(null);
    }
}
