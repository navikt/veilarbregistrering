package no.nav.fo.veilarbregistrering.oppgave;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static no.nav.fo.veilarbregistrering.oppgave.FristFerdigstillelse.fristFerdigstillelse;
import static org.assertj.core.api.Assertions.assertThat;

public class FristFerdigstillelseTest {

    @Test
    public void skal() {
        FristFerdigstillelse fristFerdigstillelse = fristFerdigstillelse(LocalDate.now());
        assertThat(fristFerdigstillelse.asLocalDate()).isEqualTo(LocalDate.now().plusDays(2));

    }
}
