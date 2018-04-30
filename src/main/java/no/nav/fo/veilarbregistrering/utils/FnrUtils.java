package no.nav.fo.veilarbregistrering.utils;

import no.bekk.bekkopen.person.Fodselsnummer;
import no.bekk.bekkopen.person.FodselsnummerValidator;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.domain.AktorId;

import java.time.LocalDate;
import java.time.Period;


public class FnrUtils {

    public static LocalDate utledFodselsdatoForFnr(String fnr) {
        Fodselsnummer fodselsnummer = FodselsnummerValidator.getFodselsnummer(fnr);

        return LocalDate.of(
                Integer.parseInt(fodselsnummer.getBirthYear()),
                Integer.parseInt(fodselsnummer.getMonth()),
                Integer.parseInt(fodselsnummer.getDayInMonth())
        );
    }

    public static int antallAarSidenDato(LocalDate dato, LocalDate dagensDato) {
        return Period.between(dato, dagensDato).getYears();
    }

    public static AktorId getAktorIdOrElseThrow(AktorService aktorService, String fnr) {
        return aktorService.getAktorId(fnr)
                .map(AktorId::new)
                .orElseThrow(() -> new IllegalArgumentException("Fant ikke aktør for fnr: " + fnr));
    }
}
