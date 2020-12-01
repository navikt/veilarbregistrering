package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;

import static java.util.Optional.ofNullable;

class ArbeidsforholdMapper {
    static Arbeidsforhold map(no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold arbeidsforhold) {
        return new Arbeidsforhold(map(arbeidsforhold.getArbeidsgiver()),
                        arbeidsforhold.getArbeidsavtale().stream()
                                .findFirst()
                                .map(Arbeidsavtale::getYrke)
                                .map(Yrker::getKodeRef)
                                .orElse("utenstyrkkode"),
                        getFom(arbeidsforhold.getAnsettelsesPeriode()),
                        getTom(arbeidsforhold.getAnsettelsesPeriode()));
    }

    private static String map(Aktoer arbeidsgiver) {
        if (arbeidsgiver instanceof Organisasjon) {
            Organisasjon organisasjon = (Organisasjon) arbeidsgiver;
            return organisasjon.getOrgnummer();
        }
        return null;
    }

    private static LocalDate getFom(AnsettelsesPeriode ansettelsesPeriode) {
        return ofNullable(ansettelsesPeriode)
                .map(AnsettelsesPeriode::getPeriode)
                .map(Periode::getFom)
                .map(ArbeidsforholdMapper::xmlGregorianCalendarToLocalDate)
                .orElse(null);
    }

    private static LocalDate getTom(AnsettelsesPeriode periode) {
        return ofNullable(periode)
                .map(AnsettelsesPeriode::getPeriode)
                .map(Periode::getTom)
                .map(ArbeidsforholdMapper::xmlGregorianCalendarToLocalDate).orElse(null);
    }

    private static LocalDate xmlGregorianCalendarToLocalDate(XMLGregorianCalendar inaktiveringsdato) {
        return ofNullable(inaktiveringsdato)
                .map(XMLGregorianCalendar::toGregorianCalendar)
                .map(GregorianCalendar::toZonedDateTime)
                .map(ZonedDateTime::toLocalDate).orElse(null);
    }
}
