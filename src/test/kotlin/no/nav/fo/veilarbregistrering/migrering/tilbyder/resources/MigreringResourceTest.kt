package no.nav.fo.veilarbregistrering.migrering

import io.mockk.*
import no.nav.fo.veilarbregistrering.config.RequestContext
import no.nav.fo.veilarbregistrering.migrering.tilbyder.Secrets
import no.nav.fo.veilarbregistrering.db.migrering.MigreringRepositoryImpl
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.migrering.tilbyder.resources.MigreringResource
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringRepository
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
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = [MigreringResourceConfig::class])
class MigreringResourceTest(
    @Autowired private val mvc: MockMvc,
    @Autowired private val migreringRepositoryImpl: MigreringRepositoryImpl
) {
    private lateinit var request: HttpServletRequest

    @BeforeEach
    fun setup() {
        clearAllMocks()
        mockkStatic(RequestContext::class)
        mockkObject(Secrets)
        every { Secrets[any()] } returns "token"
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
            status { isMethodNotAllowed() }
        }.andReturn().response.contentAsString

        assertThat(responseString).isNullOrEmpty()
    }

    @Test
    fun `hent oppdaterte statuser mapper riktig`() {
        val time = LocalDateTime.of(2021, 12, 12, 21, 20, 10)
        every { migreringRepositoryImpl.hentRegistreringTilstander(any()) } returns listOf(
            mapOf<String, Any?>(
                "ID" to 1,
                "BRUKER_REGISTRERING_ID" to 1,
                "OPPRETTET" to time,
                "SIST_ENDRET" to null,
                "STATUS" to Status.PUBLISERT_KAFKA,
            )
        )

        val responseString = mvc.post("/api/migrering/registrering-tilstand/hent-oppdaterte-statuser") {
            contentType = MediaType.APPLICATION_JSON
            content = """{ "1": "MOTTATT" }"""
            header("x-token", "token")
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        logger.info("Response: ", responseString)
        assertThat(responseString).isEqualTo("""[{"ID":1,"BRUKER_REGISTRERING_ID":1,"OPPRETTET":"$time","SIST_ENDRET":null,"STATUS":"PUBLISERT_KAFKA"}]""")
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