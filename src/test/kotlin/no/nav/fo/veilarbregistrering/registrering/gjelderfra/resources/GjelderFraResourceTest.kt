package no.nav.fo.veilarbregistrering.registrering.gjelderfra.resources

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.*
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.feil.FeilHandtering
import no.nav.fo.veilarbregistrering.registrering.gjelderfra.GjelderFraDato
import no.nav.fo.veilarbregistrering.registrering.gjelderfra.GjelderFraService
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

        assertThat(responseBody).isEqualTo(objectMapper.writeValueAsString(GjelderFraDatoResponseDto(null)))
    }

    @Test
    fun `GET - returnerer gjelderFraDato`() {
        val gjelderFraDato = GjelderFraDato(
            id = 1,
            foedselsnummer = Foedselsnummer("11"),
            dato = LocalDate.of(2020, 6, 20),
            brukerRegistreringId = 42,
            opprettetDato = LocalDateTime.now()
        )

        every { gjelderFraService.hentDato(any()) } returns gjelderFraDato

        val responseBody = mvc.get("/api/registrering/gjelder-fra")
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .response.contentAsString

        assertThat(responseBody)
            .isEqualTo(objectMapper.writeValueAsString(GjelderFraDatoResponseDto(LocalDate.of(2020, 6, 20))))
    }

    @Test
    fun `POST - returnerer 400 når sender ugyldig dato`() {
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

        every {
            gjelderFraService.opprettDato(any(), capture(postetDato))
        } just Runs

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
        every {
            gjelderFraService.opprettDato(any(), any())
        } throws RuntimeException("Noe gikk veldig galt")

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

}

@Configuration
class GjelderFraDatoResourceConfig {
    @Bean
    fun gjelderFraDatoResource() : GjelderFraDatoResource {
        return GjelderFraDatoResource(mockk(relaxed = true), mockk(relaxed = true), gjelderFraService())
    }

    @Bean
    fun gjelderFraService(): GjelderFraService = mockk<GjelderFraService>()

    @Bean
    fun feilhandtering() : FeilHandtering {
        return FeilHandtering()
    }
}
