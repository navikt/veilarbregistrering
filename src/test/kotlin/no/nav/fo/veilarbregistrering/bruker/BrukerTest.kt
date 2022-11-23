package no.nav.fo.veilarbregistrering.bruker

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class BrukerTest {

    @Test
    fun `alleFoedselsnummer skal returnere både gjeldende og historiske fødselsnummer`() {
        val bruker = Bruker(GJELDENDE_FOEDSELSNUMMER, AKTORID, listOf(FOEDSELSNUMMER_2, FOEDSELSNUMMER_3))

        assertTrue(bruker.alleFoedselsnummer().size == 3)
        assertTrue(bruker.alleFoedselsnummer().contains(GJELDENDE_FOEDSELSNUMMER))
        assertTrue(bruker.alleFoedselsnummer().containsAll(listOf(FOEDSELSNUMMER_2, FOEDSELSNUMMER_3)))
    }

    companion object {
        private val GJELDENDE_FOEDSELSNUMMER = Foedselsnummer("01234567890")
        private val AKTORID = AktorId("1000010000100")
        private val FOEDSELSNUMMER_2 = Foedselsnummer("01234567892")
        private val FOEDSELSNUMMER_3 = Foedselsnummer("01234567895")
    }
}