package no.nav.fo.veilarbregistrering.arbeidsledigDato.resources

import io.mockk.mockk
import no.nav.fo.veilarbregistrering.arbeidssoker.resources.ArbeidssokerResourceConfig
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = [ArbeidsledigDatoResourceConfig::class])
class ArbeidsledigDatoResourceTest (@Autowired private val mvc: MockMvc) {

    @Test
    fun `get - Svarer med 200 - dato=null når det ikke fins noen dato`(){
        val responseBody = mvc.get("/api/arbeidsledigDato/")
            .andExpect {
                status { isOk() }
            }

        Assertions.assertThat(responseBody).isEqualTo(ArbeidsledigDatoDto(null))
    }
}

@Configuration
class ArbeidsledigDatoResourceConfig {
    @Bean
    fun arbeidsledigDatoResource() : ArbeidsledigDatoResource {
        return ArbeidsledigDatoResource(mockk(relaxed = true), mockk(relaxed = true))
    }
}