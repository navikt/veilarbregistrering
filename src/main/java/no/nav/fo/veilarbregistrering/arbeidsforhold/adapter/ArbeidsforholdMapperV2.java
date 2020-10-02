package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;

import java.time.LocalDate;

import static java.util.Optional.ofNullable;

class ArbeidsforholdMapperV2 {
    static Arbeidsforhold map(ArbeidsforholdDto arbeidsforholdDto) {
        return new Arbeidsforhold()
                .setArbeidsgiverOrgnummer(map(arbeidsforholdDto.getArbeidsgiver()))
                .setStyrk(arbeidsforholdDto.getArbeidsavtaler().stream()
                        .findFirst()
                        .map(a -> a.getYrke())
                        .orElse("utenstyrkkode"))
                .setFom(getFom(arbeidsforholdDto.getAnsettelsesperiode()))
                .setTom(getTom(arbeidsforholdDto.getAnsettelsesperiode()));
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
                .map(ArbeidsforholdMapperV2::xmlGregorianCalendarToLocalDate)
                .orElse(null);
    }

    private static LocalDate getTom(AnsettelsesperiodeDto periode) {
        return ofNullable(periode)
                .map(AnsettelsesperiodeDto::getPeriode)
                .map(PeriodeDto::getTom)
                .map(ArbeidsforholdMapperV2::xmlGregorianCalendarToLocalDate).orElse(null);
    }

    private static LocalDate xmlGregorianCalendarToLocalDate(String dato) {
        return ofNullable(dato)
                .map(d -> LocalDate.parse(dato))
                .orElse(null);
    }
}
