package no.nav.fo.veilarbregistrering.sykemelding;

import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SykemeldingServiceTest {

    @Test
    public void skalVaereSykmeldtOverEllerLik39Uker() {
        String maksDato = "2018-10-01";
        LocalDate dagenDato = LocalDate.of(2018, Month.JUNE, 26);
        assertEquals(true, SykemeldingService.beregnSykmeldtMellom39Og52Uker(maksDato, dagenDato));
    }

    @Test
    public void skalVaereSykmeldtAkkurat52Uker() {
        String maksDato = "2018-12-11";
        LocalDate dagenDato = LocalDate.of(2018, Month.DECEMBER, 11);
        assertEquals(true, SykemeldingService.beregnSykmeldtMellom39Og52Uker(maksDato, dagenDato));
    }

    @Test
    public void skalVaereSykmeldtNesten52Uker() {
        String maksDato = "2018-12-11";
        LocalDate dagenDato = LocalDate.of(2018, Month.DECEMBER, 9);
        assertEquals(true, SykemeldingService.beregnSykmeldtMellom39Og52Uker(maksDato, dagenDato));
    }

    @Test
    public void skalIkkeVaereSykmeldtOver39Uker() {
        String maksDato = "2018-10-01";
        LocalDate dagenDato = LocalDate.of(2018, Month.APRIL, 9);
        assertEquals(false, SykemeldingService.beregnSykmeldtMellom39Og52Uker(maksDato, dagenDato));
    }

    @Test
    public void skalIkkeVaereSykmeldtOver39UkerNarMaksDatoErUnderDagensDato() {
        String maksDato = "2018-10-01";
        LocalDate dagenDato = LocalDate.of(2019, Month.APRIL, 9);
        assertEquals(false, SykemeldingService.beregnSykmeldtMellom39Og52Uker(maksDato, dagenDato));
    }

    @Test
    public void skalHandtereNullVedBeregnSykmeldtOver39uker() {
        String maksDato = null;
        LocalDate dagenDato = LocalDate.of(2019, Month.APRIL, 9);
        assertEquals(false, SykemeldingService.beregnSykmeldtMellom39Og52Uker(maksDato, dagenDato));
    }
}
