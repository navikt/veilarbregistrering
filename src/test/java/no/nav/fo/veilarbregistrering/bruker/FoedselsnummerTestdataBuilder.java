package no.nav.fo.veilarbregistrering.bruker;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static no.bekk.bekkopen.person.FodselsnummerCalculator.getFodselsnummerForDate;

public class FoedselsnummerTestdataBuilder {

    /**
     * Returnerer fødselsnummer til Aremark som er fiktivt
     */
    public static Foedselsnummer aremark() {
        return Foedselsnummer.of("10108000398");
    }

    public static String getFodselsnummerAsStringOnDateMinusYears(LocalDate localDate, int minusYears) {
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).minusYears(minusYears).toInstant());
        return getFodselsnummerForDate(date).toString();
    }

    public static Foedselsnummer fodselsnummerOnDateMinusYears(LocalDate localDate, int minusYears) {
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).minusYears(minusYears).toInstant());
        return Foedselsnummer.of(getFodselsnummerForDate(date).toString());
    }
}
