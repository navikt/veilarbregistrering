package no.nav.fo.veilarbregistrering.utils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.GregorianCalendar;
import java.util.Optional;

public class DateUtils {

    public static Timestamp toTimeStamp(String utc) {
        return Timestamp.from(ZonedDateTime.parse(utc).toInstant());
    }


    public static LocalDate xmlGregorianCalendarToLocalDate(XMLGregorianCalendar inaktiveringsdato) {
        return Optional.ofNullable(inaktiveringsdato)
                .map(XMLGregorianCalendar::toGregorianCalendar)
                .map(GregorianCalendar::toZonedDateTime)
                .map(ZonedDateTime::toLocalDate).orElse(null);
    }

    public static boolean beregnSykmeldtMellom39Og52Uker(String maksDato, LocalDate dagenDato) {
        if (maksDato == null) {
            return false;
        }
        LocalDate dato = LocalDate.parse(maksDato);
        long GJENSTAENDE_UKER = 13;

        return ChronoUnit.WEEKS.between(dagenDato, dato) >= 0 &&
                ChronoUnit.WEEKS.between(dagenDato, dato) <= GJENSTAENDE_UKER;
    }
}