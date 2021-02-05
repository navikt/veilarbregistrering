package no.nav.fo.veilarbregistrering.oppgave.resources

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.feil.FeilHandtering
import no.nav.fo.veilarbregistrering.oppgave.OppgaveAlleredeOpprettet
import no.nav.fo.veilarbregistrering.oppgave.OppgaveResponse
import no.nav.fo.veilarbregistrering.oppgave.OppgaveService
import no.nav.fo.veilarbregistrering.oppgave.OppgaveType
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = [OppgaveResourceConfig::class])
class OppgaveResourceTest(@Autowired private val mvc: MockMvc) {

    @Test
    fun `Skal opprette oppgave basert på type`() {
        val responseBody = mvc.perform(post("/api/oppgave")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"oppgaveType\": \"UTVANDRET\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.oppgaveType").value("UTVANDRET"))
                .andExpect(jsonPath("$.id").value("12313"))
                .andExpect(jsonPath("$.tildeltEnhetsnr").value("3215"))

        Assertions.assertThat(responseBody).isNotNull
    }

    @Test
    fun `Opprettelse av duplikat oppgave skal gi 403`() {
        val responseBody = mvc.perform(post("/api/oppgave")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"oppgaveType\": \"OPPHOLDSTILLATELSE\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden)

        Assertions.assertThat(responseBody).isNotNull
    }
}

@Configuration
class OppgaveResourceConfig {

    @Bean
    fun feilhandtering() : FeilHandtering {
        return FeilHandtering()
    }

    @Bean
    fun oppgaveResource(oppgaveService: OppgaveService) : OppgaveResource {
        return OppgaveResource(mockk(relaxed = true), oppgaveService, mockk(relaxed = true))
    }

    @Bean
    fun oppgaveService(): OppgaveService {
        val oppgaveService = mockk<OppgaveService>()

        every { oppgaveService.opprettOppgave(any(), OppgaveType.UTVANDRET) } returns object:OppgaveResponse {
            override fun getId(): Long = 12313
            override fun getTildeltEnhetsnr(): String = "3215"
        }

        every { oppgaveService.opprettOppgave(any(), OppgaveType.OPPHOLDSTILLATELSE) } throws OppgaveAlleredeOpprettet("test")

        return oppgaveService
    }
}