package no.nav.fo.veilarbregistrering.utils;

import no.nav.fo.veilarbregistrering.domain.InfotrygdData;
import no.nav.fo.veilarbregistrering.domain.SykmeldtInfoData;

import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZonedDateTime;
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

    public static boolean beregnSykmeldtOver39uker(String maksDato, LocalDate dagenDato) {
        LocalDate dato = LocalDate.parse(maksDato);
        long GJENSTAENDE_UKER = 13;

        return ChronoUnit.WEEKS.between(dagenDato, dato) >= 0 &&
                ChronoUnit.WEEKS.between(dagenDato, dato) <= GJENSTAENDE_UKER;
    }
}