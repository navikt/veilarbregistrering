package no.nav.fo.veilarbregistrering.arbeidssoker.resources

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerService
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeTestdataBuilder.Companion.medArbs
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperioderTestdataBuilder.Companion.arbeidssokerperioder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.LocalDate

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = [ArbeidssokerResourceConfig::class])
class ArbeidssokerResourceTest(@Autowired private val mvc: MockMvc) {

    private val BRUKER: String = "12312312312"

    @Test
    fun `Svarer med DTO med tom liste av perioder ved 404 fra aareg`() {
        val responseBody = mvc.get("/api/arbeidssoker/perioder?fnr=$BRUKER&fraOgMed=2010-01-01&tilOgMed=2021-01-01")
            .andExpect {
                status { isOk }
            }

        assertThat(responseBody).isNotNull
    }

    @Test
    fun `Gir ikke feil dersom optional felt droppes`() {
        val responseBody = mvc.get("/api/arbeidssoker/perioder?fnr=$BRUKER&fraOgMed=2010-01-01")
            .andExpect {
                status { isOk }
            }

        assertThat(responseBody).isNotNull
    }

    @Test
    fun `Gir feil dersom påkrevd felt mangler`() {
        val responseBody = mvc.get("/api/arbeidssoker/perioder?fnr=$BRUKER")
            .andExpect {
                status { isBadRequest }
            }

        assertThat(responseBody).isNotNull
    }
}

@Configuration
class ArbeidssokerResourceConfig {

    @Bean
    fun arbeidssokerResource(arbeidssokerService: ArbeidssokerService) : ArbeidssokerResource {
        return ArbeidssokerResource(arbeidssokerService, mockk(relaxed = true), mockk(relaxed = true))
    }

    @Bean
    fun arbeidssokerService(): ArbeidssokerService {
        val arbeidssokerService = mockk<ArbeidssokerService>()

        every { arbeidssokerService.hentArbeidssokerperioder(any(), any()) } returns
                arbeidssokerperioder()
                        .arbeidssokerperiode(
                                medArbs()
                                        .fra(LocalDate.of(2020, 1, 12))
                                        .til(LocalDate.of(2020, 2, 20))
                        )
                        .arbeidssokerperiode(
                                medArbs()
                                        .fra(LocalDate.of(2020, 3, 12))
                                        .til(null)
                        )
                        .build()
        return arbeidssokerService
    }
}
