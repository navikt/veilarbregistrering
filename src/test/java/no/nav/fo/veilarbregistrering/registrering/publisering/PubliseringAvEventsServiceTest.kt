package no.nav.fo.veilarbregistrering.registrering.publisering

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstand
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.LocalDateTime

internal class PubliseringAvEventsServiceTest {

    private lateinit var publiseringAvEventsService: PubliseringAvEventsService
    private lateinit var brukerRegistreringRepository: BrukerRegistreringRepository
    private lateinit var registreringTilstandReposistory: RegistreringTilstandRepository
    private lateinit var profileringRepository: ProfileringRepository


    @BeforeEach
    fun setUp() {
        brukerRegistreringRepository = mockk()
        registreringTilstandReposistory = mockk()
        profileringRepository = mockk()
        publiseringAvEventsService = PubliseringAvEventsService(
            profileringRepository,
            brukerRegistreringRepository,
            mockk(relaxed = true),
            mockk(relaxed = true),
            registreringTilstandReposistory,
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
        )
    }

    @Test
    fun `harVentendeEvents er true dersom ventende registreringer`() {
        every { registreringTilstandReposistory.hentAntallPerStatus() } returns Status.values().associateWith { if (it == Status.OVERFORT_ARENA) 2 else 0 }
        assertThat(publiseringAvEventsService.harVentendeEvents()).isTrue
    }

    @Test
    fun `harVentendeEvents er false dersom ingen ventende registreringer`() {
        every { registreringTilstandReposistory.hentAntallPerStatus() } returns Status.values().associateWith { 0 }
        assertThat(publiseringAvEventsService.harVentendeEvents()).isFalse
    }

    @Test
    fun `Ingenting skjer dersom 0 events klare til publisering`() {
        every { registreringTilstandReposistory.finnNesteRegistreringTilstanderMed(any(), any()) } returns emptyList()
        publiseringAvEventsService.publiserMeldingerForRegistreringer()
    }

    @Test
    fun `Prosesserer 2 events klare til publisering`() {
        every { registreringTilstandReposistory.finnNesteRegistreringTilstanderMed(any(), any()) } returns listOf(
            RegistreringTilstand.of(100, 1, now, now, Status.OVERFORT_ARENA),
            RegistreringTilstand.of(101, 2, now, now, Status.OVERFORT_ARENA),
        )
        every { brukerRegistreringRepository.hentBrukerregistreringerForIder(listOf(1, 2)) } returns toRegistreringer
        every { profileringRepository.hentProfileringerForIder(listOf(1, 2))} returns toProfileringer
        every { registreringTilstandReposistory.oppdaterFlereTilstander(Status.PUBLISERT_KAFKA, listOf(100, 101))} just runs
        assertDoesNotThrow { publiseringAvEventsService.publiserMeldingerForRegistreringer() }
    }

    companion object {
        val toRegistreringer = mapOf(
            1L to (AktorId.of("10001") to OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering()),
            2L to (AktorId.of("10002") to OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering()),
        )

        val toProfileringer = mapOf(
            1L to ProfileringTestdataBuilder.lagProfilering(),
            2L to ProfileringTestdataBuilder.lagProfilering(),
        )

        private val now = LocalDateTime.now()
    }
}