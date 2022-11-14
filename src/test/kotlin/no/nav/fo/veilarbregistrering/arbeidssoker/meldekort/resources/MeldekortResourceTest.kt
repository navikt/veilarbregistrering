package no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.resources

import io.mockk.every
import io.mockk.mockk
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortPeriode
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortService
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.Meldekorttype
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder
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
import java.time.LocalDate
import java.time.LocalDateTime

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = [MeldekortResourceConfig::class])
internal class MeldekortResourceTest(@Autowired private val mvc: MockMvc) {

    @Test
    fun `get - Svarer med en liste med meldekort`() {
        val responseBody = mvc.get("/api/arbeidssoker/meldekort")
            .andExpect {
                status { isOk() }
            }

        Assertions.assertThat(responseBody).isNotNull
    }
}

@Configuration
class MeldekortResourceConfig {
    @Bean
    fun meldekortResource(meldekortService: MeldekortService): MeldekortResource {
        return MeldekortResource(mockk(relaxed = true), mockk(relaxed = true), meldekortService)
    }

    @Bean
    fun meldekortService(): MeldekortService {
        val meldekortService = mockk<MeldekortService>()
        every { meldekortService.hentMeldekort(any()) } returns listOf(
            MeldekortEvent(
                FoedselsnummerTestdataBuilder.aremark(),
                true,
                MeldekortPeriode(
                    LocalDate.now(),
                    LocalDate.now()
                ),
                Meldekorttype.MANUELL_ARENA,
                1,
                LocalDateTime.now()
            )
        )
        return meldekortService
    }
}
