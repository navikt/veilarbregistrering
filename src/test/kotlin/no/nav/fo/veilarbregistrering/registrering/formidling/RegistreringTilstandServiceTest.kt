package no.nav.fo.veilarbregistrering.registrering.formidling

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandTestdataBuilder.registreringTilstand
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class RegistreringTilstandServiceTest {

    private lateinit var registreringTilstandRepository: RegistreringTilstandRepository
    private lateinit var registreringTilstandService: RegistreringTilstandService

    @BeforeEach
    fun setup() {
        registreringTilstandRepository = mockk()
        registreringTilstandService = RegistreringTilstandService(registreringTilstandRepository)
    }

    @Test
    fun `skal oppdatere registreringtilstand med status og sistendret`() {
        val sistEndret = LocalDateTime.now()
        val original = registreringTilstand()
            .status(Status.MOTTATT)
            .opprettet(sistEndret.minusDays(1))
            .sistEndret(sistEndret)
            .build()

        every { registreringTilstandRepository.hentRegistreringTilstand(original.id) } returns original
        val registreringTilstandSlot = slot<RegistreringTilstand>()
        every { registreringTilstandRepository.oppdater(capture(registreringTilstandSlot)) } returns original

        registreringTilstandService.oppdaterRegistreringTilstand(
            OppdaterRegistreringTilstandCommand.of(
                original.id,
                Status.OVERFORT_ARENA
            )
        )

        val capturedArgument = registreringTilstandSlot.captured
        assertThat(capturedArgument.id).isEqualTo(original.id)
        assertThat(capturedArgument.brukerRegistreringId).isEqualTo(original.brukerRegistreringId)
        assertThat(capturedArgument.opprettet).isEqualTo(original.opprettet)
        assertThat(capturedArgument.status).isEqualTo(Status.OVERFORT_ARENA)
    }
}
