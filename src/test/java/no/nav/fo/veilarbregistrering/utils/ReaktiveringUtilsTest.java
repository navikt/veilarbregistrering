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
    public void harVartRegistrertSiste28dager() {
        assertEquals(ReaktiveringUtils.harVartRegistrertSiste28dager(LocalDate.now().minusDays(24)), true);
        assertEquals(ReaktiveringUtils.harVartRegistrertSiste28dager(LocalDate.now().minusDays(28)), true);
    }

    @Test
    public void harIkkeVartRegistrertSiste28dager() {
        assertEquals(ReaktiveringUtils.harVartRegistrertSiste28dager(LocalDate.now().minusDays(31)), false);
        assertEquals(ReaktiveringUtils.harVartRegistrertSiste28dager(LocalDate.now().minusDays(29)), false);
    }
}