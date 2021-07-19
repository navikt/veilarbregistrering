package no.nav.fo.veilarbregistrering.bruker

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FoedselsnummerTest {

    @Test
    fun `maskert skal maskere alle tegn med stjerne`() {
        val foedselsnummer = Foedselsnummer.of("23067822521")
        assertThat(foedselsnummer.maskert()).isEqualTo("***********")
    }
}
