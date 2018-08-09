package no.nav.fo.veilarbregistrering.utils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZonedDateTime;
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

}