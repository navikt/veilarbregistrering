package no.nav.fo.veilarbregistrering

import no.nav.fo.veilarbregistrering.config.ApplicationTestConfig
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@EnableAutoConfiguration
@AutoConfigureMockMvc
@SpringJUnitConfig(classes = [ApplicationTestConfig::class])
class SpringContextTest(
    @Autowired private val context: ApplicationContext,
    @Autowired private val mvc: MockMvc,
) {

    @Test
    fun `spring context lastes uten feil`() {
        Assertions.assertThat(context).isNotNull
    }

    @Test
    fun `selftest svarer med html`() {
        mvc.get("/internal/selftest").andExpect {
            status { isOk() }
        }
    }
}