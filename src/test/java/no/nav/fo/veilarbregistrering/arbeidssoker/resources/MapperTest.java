package no.nav.fo.veilarbregistrering.arbeidssoker.resources;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class MapperTest {

    @Test
    public void skal() {
        LocalDate now = LocalDate.now();
        assertThat(now.toString()).isEqualTo("2020-06-26");
    }
}
