package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.bekk.bekkopen.person.Fodselsnummer;
import no.bekk.bekkopen.person.FodselsnummerValidator;

import java.time.LocalDate;
import java.time.Period;


public class FnrUtils {

    public static int utledAlderForFnr(String fnr, LocalDate dagensDato) {
        return antallAarSidenDato(utledFodselsdatoForFnr(fnr), dagensDato);
    }

    static LocalDate utledFodselsdatoForFnr(String fnr) {
        Fodselsnummer fodselsnummer = FodselsnummerValidator.getFodselsnummer(fnr);

        return LocalDate.of(
                Integer.parseInt(fodselsnummer.getBirthYear()),
                Integer.parseInt(fodselsnummer.getMonth()),
                Integer.parseInt(fodselsnummer.getDayInMonth())
        );
    }

    static int antallAarSidenDato(LocalDate dato, LocalDate dagensDato) {
        return Period.between(dato, dagensDato).getYears();
    }

}
