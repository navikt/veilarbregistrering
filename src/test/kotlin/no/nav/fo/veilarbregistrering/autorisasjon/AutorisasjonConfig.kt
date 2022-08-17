package no.nav.fo.veilarbregistrering.autorisasjon

import io.mockk.mockk
import no.nav.common.abac.Pep
import no.nav.common.auth.context.AuthContextHolder
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AutorisasjonConfig {
    @Bean
    fun pepClient(): Pep = mockk()

    @Bean
    fun autorisasjonService(
        veilarbPep: Pep,
        authContextHolder: AuthContextHolder,
        metricsService: MetricsService
    ): AutorisasjonService {
        return DefaultAutorisasjonService(veilarbPep, authContextHolder, metricsService)
    }
}
