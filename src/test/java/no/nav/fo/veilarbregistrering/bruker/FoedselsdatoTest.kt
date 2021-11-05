package no.nav.fo.veilarbregistrering.bruker

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class FoedselsdatoTest {

    @Test
    fun `alder øker ikke for fødselsdag`() {
        val foedselsdato = Foedselsdato(LocalDate.of(1978, 6, 23))

        assertThat(foedselsdato.alderPaa(LocalDate.of(2020, 2, 20))).isEqualTo(41)
    }

    @Test
    fun `fødselsdag øker alder`() {
        val foedselsdato = Foedselsdato(LocalDate.of(1978, 6, 23))

        assertThat(foedselsdato.alderPaa(LocalDate.of(2020, 6, 23))).isEqualTo(42)
    }
}
