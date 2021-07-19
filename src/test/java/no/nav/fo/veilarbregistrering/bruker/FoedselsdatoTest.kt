package no.nav.fo.veilarbregistrering.bruker

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class FoedselsdatoTest {

    @Test
    fun `alder øker ikke for fødselsdag`() {
        val foedselsdato: Foedselsdato = object : Foedselsdato(LocalDate.of(1978, 6, 23)) {
            override fun dagensDato(): LocalDate {
                return LocalDate.of(2020, 2, 20)
            }
        }
        assertThat(foedselsdato.alder()).isEqualTo(41)
    }

    @Test
    fun `fødselsdag øker alder`() {
        val foedselsdato: Foedselsdato = object : Foedselsdato(LocalDate.of(1978, 6, 23)) {
            override fun dagensDato(): LocalDate {
                return LocalDate.of(2020, 6, 23)
            }
        }
        assertThat(foedselsdato.alder()).isEqualTo(42)
    }
}
