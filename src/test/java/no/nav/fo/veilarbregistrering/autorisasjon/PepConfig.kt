package no.nav.fo.veilarbregistrering.autorisasjon

import io.mockk.mockk
import no.nav.common.abac.Pep
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PepConfig {
    @Bean
    fun pepClient(): Pep = mockk()
}
