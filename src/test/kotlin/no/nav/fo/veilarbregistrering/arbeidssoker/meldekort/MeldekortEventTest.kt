package no.nav.fo.veilarbregistrering.arbeidssoker.meldekort

import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MeldekortEventTest {

    @Test
    fun `Meldekort siste 14 dager`(){
        val meldekortEvent = MeldekortEvent(
            FoedselsnummerTestdataBuilder.aremark(),
            true,
            MeldekortPeriode(
                LocalDate.now(),
                LocalDate.now()
            ),
            Meldekorttype.MANUELL_ARENA,
            1,
            LocalDateTime.now()
        )

        val siste14Dager = meldekortEvent.erSendtInnSiste14Dager()
        assertTrue(siste14Dager)

    }

    @Test
    fun `Meldekort eldre en 14 dager`(){
        val meldekortEvent = MeldekortEvent(
            FoedselsnummerTestdataBuilder.aremark(),
            true,
            MeldekortPeriode(
                LocalDate.now(),
                LocalDate.now()
            ),
            Meldekorttype.MANUELL_ARENA,
            1,
            LocalDateTime.now().minusMonths(1)
        )

        val siste14Dager = meldekortEvent.erSendtInnSiste14Dager()
        assertFalse(siste14Dager)
    }

}