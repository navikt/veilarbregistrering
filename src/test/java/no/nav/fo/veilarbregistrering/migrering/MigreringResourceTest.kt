package no.nav.fo.veilarbregistrering.migrering

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.db.migrering.MigreringRepositoryImpl
import no.nav.fo.veilarbregistrering.db.migrering.MigreringResource
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstand
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.nio.file.Files
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = [MigreringResourceConfig::class])
class RegistreringResourceTest(
    @Autowired private val mvc: MockMvc,
    @Autowired private val registreringTilstandRepository: RegistreringTilstandRepository,
) {
    private lateinit var request: HttpServletRequest

    @BeforeEach
    fun setup() {
        clearAllMocks()
        mockkStatic(RequestContext::class)
        mockkStatic(Files::class)
        every { Files.readAllBytes(any()) } returns "token".toByteArray()
        request = mockk(relaxed = true)
        every { RequestContext.servletRequest() } returns request
    }

    @Test
    fun `hent oppdaterte statuser krever post`() {
        val responseString = mvc.get("/api/migrering/registrering-tilstand/hent-oppdaterte-statuser") {
            contentType = MediaType.APPLICATION_JSON
            content = """{ 1: "MOTTATT" }"""
            header("x-token", "token")
        }.andExpect {
            status { isMethodNotAllowed }
        }.andReturn().response.contentAsString

        assertThat(responseString).isNullOrEmpty()
    }

    @Test
    fun `hent oppdaterte statuser mapper riktig`() {
        every { registreringTilstandRepository.hentRegistreringTilstander(any()) } returns listOf(
            RegistreringTilstand.of(
                1,
                1,
                LocalDateTime.now().minusDays(1),
                null,
                Status.PUBLISERT_KAFKA,
            )
        )

        val responseString = mvc.post("/api/migrering/registrering-tilstand/hent-oppdaterte-statuser") {
            contentType = MediaType.APPLICATION_JSON
            content = """{ "1": "MOTTATT" }"""
            header("x-token", "token")
        }.andExpect {
            status { isOk }
        }.andReturn().response.contentAsString
        logger.info("Response: ", responseString)
        assertThat(responseString).isEqualTo("""{"PUBLISERT_KAFKA":[1]}""")
    }
}
@Configuration
private class MigreringResourceConfig {
    @Bean
    fun migreringResource(
        migreringRepositoryImpl: MigreringRepositoryImpl,
            brukerRegistreringRepository: BrukerRegistreringRepository,
            registreringTilstandRepository: RegistreringTilstandRepository,
            ) = MigreringResource(
        migreringRepositoryImpl,
        brukerRegistreringRepository,
        registreringTilstandRepository,
    )
    @Bean
    fun migreringRepositoryImpl(): MigreringRepositoryImpl = mockk(relaxed = true)

    @Bean
    fun brukerRegistreringRepository(): BrukerRegistreringRepository = mockk(relaxed = true)

    @Bean
    fun registreringTilstandRepository(): RegistreringTilstandRepository = mockk(relaxed = true)

}