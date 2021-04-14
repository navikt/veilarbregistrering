package no.nav.fo.veilarbregistrering.tidslinje

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerRepository
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.bruker.ReaktiveringRepository
import no.nav.fo.veilarbregistrering.registrering.bruker.ReaktiveringTestdataBuilder.gyldigReaktivering
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

class TidslinjeAggregatorTest {

    private lateinit var brukerRegistreringRepository: BrukerRegistreringRepository
    private lateinit var sykmeldtRegistreringRepository: SykmeldtRegistreringRepository
    private lateinit var reaktiveringRepository: ReaktiveringRepository
    private lateinit var arbeidssokerperiodeRepository: ArbeidssokerRepository

    private lateinit var tidslinjeAggregator: TidslinjeAggregator

    @BeforeEach
    fun setUp() {
        brukerRegistreringRepository = mockk(relaxed = true)
        sykmeldtRegistreringRepository = mockk(relaxed = true)
        reaktiveringRepository = mockk(relaxed = true)
        arbeidssokerperiodeRepository = mockk(relaxed = true)

        tidslinjeAggregator = TidslinjeAggregator(
                brukerRegistreringRepository,
                sykmeldtRegistreringRepository,
                reaktiveringRepository,
                arbeidssokerperiodeRepository)
    }

    @Test
    fun `tidslinje skal returnere tom liste når ingen treff eller data`() {
        val tidslinje = tidslinjeAggregator.tidslinje(testBruker)

        assertThat(tidslinje).isEmpty()
    }

    @Test
    fun `tidslinje skal returnere en liste over historiske elementer fra ordinær, sykmeldt og reaktivering`() {
        every { brukerRegistreringRepository.finnOrdinaerBrukerregistreringForAktorIdOgTilstand(any(), any()) } returns Arrays.asList(gyldigBrukerRegistrering())
        every { sykmeldtRegistreringRepository.finnSykmeldtRegistreringerFor(any()) } returns Arrays.asList(gyldigSykmeldtRegistrering())
        every { reaktiveringRepository.finnReaktiveringer(any()) } returns Arrays.asList(gyldigReaktivering(testBruker.aktorId))

        val tidslinje = tidslinjeAggregator.tidslinje(testBruker)

        assertThat(tidslinje).hasSize(3)
    }

    @Test
    fun `tidslinjen skal være sortert etter perioden sin startdato`() {
        val gyldigBrukerRegistrering = gyldigBrukerRegistrering(LocalDate.of(2016, 1, 1).atStartOfDay())
        every { brukerRegistreringRepository.finnOrdinaerBrukerregistreringForAktorIdOgTilstand(any(), any()) } returns Arrays.asList(gyldigBrukerRegistrering)
        val gyldigSykmeldtRegistrering = gyldigSykmeldtRegistrering(LocalDate.of(2019, 1, 1).atStartOfDay())
        every { sykmeldtRegistreringRepository.finnSykmeldtRegistreringerFor(any()) } returns Arrays.asList(gyldigSykmeldtRegistrering)
        val gyldigReaktivering = gyldigReaktivering(testBruker.aktorId, LocalDate.of(2018, 1, 1).atStartOfDay())
        every { reaktiveringRepository.finnReaktiveringer(any()) } returns Arrays.asList(gyldigReaktivering)

        val tidslinje = tidslinjeAggregator.tidslinje(testBruker)

        assertThat(tidslinje.get(0).periode().fra).isEqualTo(gyldigBrukerRegistrering.opprettetDato.toLocalDate())
        assertThat(tidslinje.get(1).periode().fra).isEqualTo(gyldigReaktivering.opprettetTidspunkt.toLocalDate())
        assertThat(tidslinje.get(2).periode().fra).isEqualTo(gyldigSykmeldtRegistrering.opprettetDato.toLocalDate())
    }

    companion object {
        private val testBruker = Bruker.of(Foedselsnummer.of("11019141466"), AktorId.of("1"), emptyList())
    }
}