package no.nav.fo.veilarbregistrering.registrering.gjelderfra.resources

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder
import no.nav.fo.veilarbregistrering.registrering.bruker.HentRegistreringService
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.BrukerRegistreringWrapper
import no.nav.fo.veilarbregistrering.registrering.gjelderfra.GjelderFraDato
import no.nav.fo.veilarbregistrering.registrering.gjelderfra.GjelderFraService
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistreringTestdataBuilder
import org.assertj.core.api.Assertions.assertThat
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
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = [GjelderFraDatoResourceConfig::class])
class GjelderFraResourceTest (
    @Autowired private val mvc: MockMvc,
    @Autowired private val hentRegistreringService: HentRegistreringService,
    @Autowired private val gjelderFraService: GjelderFraService
    ) {

    private val objectMapper: ObjectMapper = jacksonObjectMapper()
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .registerModule(
            JavaTimeModule()
        );

    @Test
    fun `GET - Svarer med 200 - dato=null når det ikke fins noen dato`(){
        every { gjelderFraService.hentDato(any()) } returns null

        val responseBody = mvc.get("/api/registrering/gjelder-fra")
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .response.contentAsString

        assertThat(responseBody).isEqualTo(objectMapper.writeValueAsString(GjelderFraDatoDto(null)))
    }

    @Test
    fun `GET - returnerer gjelderFraDato`() {
        val gjelderFraDato = GjelderFraDato(bruker, ordinaerBrukerRegistrering, LocalDate.of(2020, 6, 20))

        every { gjelderFraService.hentDato(any()) } returns gjelderFraDato

        val responseBody = mvc.get("/api/registrering/gjelder-fra")
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .response.contentAsString

        assertThat(responseBody)
            .isEqualTo(objectMapper.writeValueAsString(GjelderFraDatoDto(LocalDate.of(2020, 6, 20))))
    }

    @Test
    fun `POST - returnerer 400 når bruker ikke har aktiv registrering`() {
        every { hentRegistreringService.hentBrukerregistreringUtenMetrics(any()) } returns null

        mvc.post("/api/registrering/gjelder-fra") {
                contentType = MediaType.APPLICATION_JSON
                content = """{ "dato": null }"""
            }
            .andExpect {
                status {
                    isBadRequest()
                }
            }.andReturn()
    }

    @Test
    fun `POST - returnerer 400 når sender ugyldig dato`() {
        every { hentRegistreringService.hentBrukerregistreringUtenMetrics(any()) } returns BrukerRegistreringWrapper(ordinaerBrukerRegistrering)
        every { gjelderFraService.opprettDato(any(), any(), any()) } returns GjelderFraDato(
            bruker,
            ordinaerBrukerRegistrering,
            LocalDate.of(2022, 6, 21)
        )

        mvc.post("/api/registrering/gjelder-fra") {
            contentType = MediaType.APPLICATION_JSON
            content = """{ "dato": null }"""
        }
            .andExpect {
                status {
                    isBadRequest()
                }
            }.andReturn()
    }

    @Test
    fun `POST - returnerer 201 når registrering går OK`() {
        val postetDato = slot<LocalDate>()

        every { hentRegistreringService.hentBrukerregistreringUtenMetrics(any()) } returns BrukerRegistreringWrapper(ordinaerBrukerRegistrering)
        every {
            gjelderFraService.opprettDato(any(), ordinaerBrukerRegistrering, capture(postetDato))
        } returns GjelderFraDato(
            bruker,
            ordinaerBrukerRegistrering,
            LocalDate.of(2022, 6, 21)
        )

        mvc.post("/api/registrering/gjelder-fra") {
            contentType = MediaType.APPLICATION_JSON
            content = """{ "dato": "2022-06-21" }"""
        }
            .andExpect {
                status {
                    isCreated()
                }
            }.andReturn()

        assertEquals(LocalDate.of(2022, 6, 21), postetDato.captured)
    }

    @Test
    fun `POST - returnerer 500 når lagring feiler`() {
        every { hentRegistreringService.hentBrukerregistreringUtenMetrics(any()) } returns BrukerRegistreringWrapper(ordinaerBrukerRegistrering)
        every {
            gjelderFraService.opprettDato(any(), ordinaerBrukerRegistrering, any())
        } throws Exception("Noe gikk veldig galt")

        mvc.post("/api/registrering/gjelder-fra") {
            contentType = MediaType.APPLICATION_JSON
            content = """{ "dato": "2022-06-21" }"""
        }
            .andExpect {
                status {
                    isInternalServerError()
                }
            }.andReturn()
    }

    companion object {
        private val fnr = Foedselsnummer("11017724129")
        private val aktorId = AktorId("12311")
        private val bruker = Bruker(fnr, aktorId)

        private val igaar = LocalDateTime.now().minusDays(1)

        private val profilering = ProfileringTestdataBuilder.lagProfilering()
        private val ordinaerBrukerRegistrering = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering(
            opprettetDato = igaar,
            profilering = profilering
        )
    }
}

@Configuration
class GjelderFraDatoResourceConfig {
    @Bean
    fun gjelderFraDatoResource() : GjelderFraDatoResource {
        return GjelderFraDatoResource(mockk(relaxed = true), mockk(relaxed = true), hentRegistreringService(), hentGjelderFraService())
    }

    @Bean
    fun hentRegistreringService(): HentRegistreringService = mockk()

    @Bean
    fun hentGjelderFraService(): GjelderFraService = mockk<GjelderFraService>()
}
