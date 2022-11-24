package no.nav.fo.veilarbregistrering.arbeidssoker.meldekort

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class MeldekortServiceTest {
    private lateinit var meldekortRepository: MeldekortRepository
    private lateinit var meldekortService: MeldekortService

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

    @BeforeEach
    fun setup() {
        meldekortRepository = mockk()
        meldekortService = MeldekortService(meldekortRepository)

        every { meldekortRepository.hent(any()) } returns listOf(meldekortEvent, meldekortEvent)
    }

    @Test
    fun `Henter meldekort`() {
        assertThat(meldekortService.hentMeldekort(FoedselsnummerTestdataBuilder.aremark()).size).isEqualTo(2)
    }

    @Test
    fun `Henter siste meldekort`() {
        assertThat(meldekortService.hentSisteMeldekort(FoedselsnummerTestdataBuilder.aremark())).isEqualTo(
            meldekortEvent
        )
    }

    @Test
    fun `Meldekort siste 14 dager`(){
        val siste14Dager = meldekortService.sisteMeldekortErSendtInnSiste14Dager(meldekortEvent)
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

        val siste14Dager = meldekortService.sisteMeldekortErSendtInnSiste14Dager(meldekortEvent)
        assertFalse(siste14Dager)


    }

}
