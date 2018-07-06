package no.nav.fo.veilarbregistrering.utils;

import no.nav.fo.veilarbregistrering.domain.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.domain.Profilering;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static no.bekk.bekkopen.person.FodselsnummerCalculator.getFodselsnummerForDate;

public class TestUtils {

    public static String getFodselsnummerOnDateMinusYears(LocalDate localDate, int minusYears) {
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).minusYears(minusYears).toInstant());
        return getFodselsnummerForDate(date).toString();
    }

    public static String getFodselsnummerForPersonWithAge(int age) {
        return getFodselsnummerOnDateMinusYears(LocalDate.now(), age);
    }

    public static Profilering lagProfilering() {
        return new Profilering()
                .setInnsatsgruppe(Innsatsgruppe.STANDARD_INNSATS)
                .setAlder(62)
                .setJobbetSammenhengendeSeksAvTolvSisteManeder(false);
    }
}
