package no.nav.fo.veilarbregistrering.registrering.gjelderfra.resources

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.config.objectMapper
import no.nav.fo.veilarbregistrering.registrering.bruker.HentRegistreringService
import org.assertj.core.api.Assertions
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

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = [GjelderFraDatoResourceConfig::class])
class GjelderFraResourceTest (@Autowired private val mvc: MockMvc, @Autowired private val hentRegistreringService: HentRegistreringService) {

    @Test
    fun `get - Svarer med 200 - dato=null når det ikke fins noen dato`(){
        val responseBody = mvc.get("/api/registrering/gjelder-fra")
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .response.contentAsString

        Assertions.assertThat(responseBody).isEqualTo(objectMapper.writeValueAsString(GjelderFraDatoDto(null)))
    }

    @Test
    fun `post - returnerer 400 når bruker ikke har aktiv registrering`() {
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
}

@Configuration
class GjelderFraDatoResourceConfig {
    @Bean
    fun gjelderFraDatoResource() : GjelderFraDatoResource {
        return GjelderFraDatoResource(mockk(relaxed = true), mockk(relaxed = true), hentRegistreringService())
    }

    @Bean
    fun hentRegistreringService(): HentRegistreringService = mockk()
}
