package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringService;
import org.junit.Test;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateUtilsTest {
    @Test
    public void toTimeStamp()  {
        String utcString = "2017-04-19T12:21:04.963+02:00";
        String expectedString = "2017-04-19 12:21:04.963";
        Timestamp timestamp = Timestamp.from(ZonedDateTime.parse(utcString).toInstant());
        String result = timestamp.toString();
        assertEquals(expectedString, result);
    }

    @Test
    public void toZonedDateTime()  {
        Timestamp timestamp = Timestamp.valueOf("2017-04-19 12:21:04.963");
        String expectedString = "2017-04-19T12:21:04.963+02:00";
        ZonedDateTime zoned = ZonedDateTime.of(timestamp.toLocalDateTime(), ZoneId.of("+02:00"));
        assertEquals(expectedString, zoned.toString());
    }

    @Test
    public void skalVaereSykmeldtOverEllerLik39Uker() {
        String maksDato = "2018-10-01";
        LocalDate dagenDato = LocalDate.of(2018, Month.JUNE, 26);
        assertEquals(true, BrukerRegistreringService.beregnSykmeldtMellom39Og52Uker(maksDato, dagenDato));
    }

    @Test
    public void skalVaereSykmeldtAkkurat52Uker() {
        String maksDato = "2018-12-11";
        LocalDate dagenDato = LocalDate.of(2018, Month.DECEMBER, 11);
        assertEquals(true, BrukerRegistreringService.beregnSykmeldtMellom39Og52Uker(maksDato, dagenDato));
    }

    @Test
    public void skalVaereSykmeldtNesten52Uker() {
        String maksDato = "2018-12-11";
        LocalDate dagenDato = LocalDate.of(2018, Month.DECEMBER, 9);
        assertEquals(true, BrukerRegistreringService.beregnSykmeldtMellom39Og52Uker(maksDato, dagenDato));
    }

    @Test
    public void skalIkkeVaereSykmeldtOver39Uker() {
        String maksDato = "2018-10-01";
        LocalDate dagenDato = LocalDate.of(2018, Month.APRIL, 9);
        assertEquals(false, BrukerRegistreringService.beregnSykmeldtMellom39Og52Uker(maksDato, dagenDato));
    }

    @Test
    public void skalIkkeVaereSykmeldtOver39UkerNarMaksDatoErUnderDagensDato() {
        String maksDato = "2018-10-01";
        LocalDate dagenDato = LocalDate.of(2019, Month.APRIL, 9);
        assertEquals(false, BrukerRegistreringService.beregnSykmeldtMellom39Og52Uker(maksDato, dagenDato));
    }

    @Test
    public void skalHandtereNullVedBeregnSykmeldtOver39uker() {
        String maksDato = null;
        LocalDate dagenDato = LocalDate.of(2019, Month.APRIL, 9);
        assertEquals(false, BrukerRegistreringService.beregnSykmeldtMellom39Og52Uker(maksDato, dagenDato));
    }
}
