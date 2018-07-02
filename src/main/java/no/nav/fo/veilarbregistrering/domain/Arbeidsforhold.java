package no.nav.fo.veilarbregistrering.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import no.nav.fo.veilarbregistrering.utils.DateUtils;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.AnsettelsesPeriode;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsavtale;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Periode;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Yrker;

import java.time.LocalDate;

import static java.util.Optional.ofNullable;

@Data
@Accessors(chain = true)
public class Arbeidsforhold {
    private String arbeidsgiverOrgnummer;
    private String styrk;
    private LocalDate fom;
    private LocalDate tom;

    public static Arbeidsforhold of(no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold arbeidsforhold) {
        return new Arbeidsforhold().setArbeidsgiverOrgnummer(arbeidsforhold.getArbeidsgiver().getAktoerId())
                .setStyrk(arbeidsforhold.getArbeidsavtale().stream().findFirst().map(Arbeidsavtale::getYrke).map(Yrker::getKodeRef).orElse("utenstyrkkode"))
                .setFom(getFom(arbeidsforhold.getAnsettelsesPeriode()))
                .setTom(getTom(arbeidsforhold.getAnsettelsesPeriode()));
    }

    private static LocalDate getFom(AnsettelsesPeriode ansettelsesPeriode) {
        return ofNullable(ansettelsesPeriode)
                .map(AnsettelsesPeriode::getPeriode)
                .map(Periode::getFom)
                .map(DateUtils::xmlGregorianCalendarToLocalDate)
                .orElse(null);
    }

    private static LocalDate getTom(AnsettelsesPeriode periode) {
        return ofNullable(periode)
                .map(AnsettelsesPeriode::getPeriode)
                .map(Periode::getTom)
                .map(DateUtils::xmlGregorianCalendarToLocalDate).orElse(null);
    }
}
