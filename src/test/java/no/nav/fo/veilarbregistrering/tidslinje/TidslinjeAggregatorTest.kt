package no.nav.fo.veilarbregistrering.tidslinje

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.registrering.bruker.*
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import java.time.LocalDateTime
import java.util.*

class TidslinjeAggregatorTest {

    private lateinit var brukerRegistreringRepository: BrukerRegistreringRepository
    private lateinit var sykmeldtRegistreringRepository: SykmeldtRegistreringRepository
    private lateinit var reaktiveringRepository: ReaktiveringRepository

    private lateinit var tidslinjeAggregator: TidslinjeAggregator

    @BeforeEach
    fun setUp() {
        brukerRegistreringRepository = mockk(relaxed = true)
        sykmeldtRegistreringRepository = mockk(relaxed = true)
        reaktiveringRepository = mockk(relaxed = true)

        tidslinjeAggregator = TidslinjeAggregator(brukerRegistreringRepository, sykmeldtRegistreringRepository, reaktiveringRepository)
    }

    @Test
    fun `tidslinje skal returnere tom liste når ingen treff eller data`() {
        val tidslinje = tidslinjeAggregator.tidslinje(testBruker)

        assertThat(tidslinje).isEmpty()
    }

    @Test
    fun `tidslinje skal returnere en liste over historiske elementer fra ordinær, sykmeldt og reaktivering`() {
        every { brukerRegistreringRepository.finnOrdinaerBrukerregistreringerFor(any()) } returns Arrays.asList(gyldigBrukerRegistrering())
        every { sykmeldtRegistreringRepository.finnSykmeldtRegistreringerFor(any()) } returns Arrays.asList(gyldigSykmeldtRegistrering())
        every { reaktiveringRepository.finnReaktiveringer(any()) } returns Arrays.asList(Reaktivering(1, testBruker.aktorId, LocalDateTime.now()))

        val tidslinje = tidslinjeAggregator.tidslinje(testBruker)

        assertThat(tidslinje).hasSize(3)
    }

    companion object {
        private val testBruker = Bruker.of(Foedselsnummer.of("11019141466"), AktorId.of("1"), emptyList())
    }
}