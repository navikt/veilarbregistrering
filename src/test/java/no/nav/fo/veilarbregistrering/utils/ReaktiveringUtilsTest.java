package no.nav.fo.veilarbregistrering.utils;

import no.nav.fo.veilarbregistrering.domain.AktivStatus;
import org.junit.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReaktiveringUtilsTest {
    @Test
    public void kreverReaktivering() {
        AktivStatus aktivStatus = new AktivStatus()
                .withAktiv(false)
                .withInaktiveringDato(LocalDate.now().minusDays(25));

        assertEquals(ReaktiveringUtils.kreverReaktivering(aktivStatus), true);
    }

    @Test
    public void harVaertRegistrertILopetAvDeSiste28Dagene() {
        assertEquals(ReaktiveringUtils.harVaertRegistrertILopetAvDeSiste28Dagene(LocalDate.now().minusDays(24)), true);
        assertEquals(ReaktiveringUtils.harVaertRegistrertILopetAvDeSiste28Dagene(LocalDate.now().minusDays(28)), true);

    }

    @Test
    public void harIkkeVaertRegistrertILopetAvDeSiste28Dagene() {
        assertEquals(ReaktiveringUtils.harVaertRegistrertILopetAvDeSiste28Dagene(LocalDate.now().minusDays(31)), false);
        assertEquals(ReaktiveringUtils.harVaertRegistrertILopetAvDeSiste28Dagene(LocalDate.now().minusDays(29)), false);
    }

    @Test
    public void harIkkeVaertRegistrertPaaOvertEttAAr() {
        assertEquals(ReaktiveringUtils.harVaertRegistrertILopetAvDeSiste28Dagene(LocalDate.now().minusYears(1).minusDays(24)), false);
        assertEquals(ReaktiveringUtils.harVaertRegistrertILopetAvDeSiste28Dagene(LocalDate.now().minusYears(1).minusDays(38)), false);
    }
}