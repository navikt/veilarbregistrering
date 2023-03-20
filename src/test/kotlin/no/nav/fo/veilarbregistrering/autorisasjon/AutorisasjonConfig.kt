package no.nav.fo.veilarbregistrering.autorisasjon

import io.mockk.mockk
import no.nav.common.abac.Pep
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.UserRole
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AutorisasjonConfig {

    @Bean
    fun pepClient(): Pep = mockk()

    @Bean
    fun tilgangskontrollService(
        veilarbPep: Pep,
        authContextHolder: AuthContextHolder,
        metricsService: MetricsService
    ): TilgangskontrollService {
        val autorisasjonServiceMap = mapOf(
            UserRole.EKSTERN to PersonbrukerAutorisasjonService(veilarbPep, authContextHolder),
            UserRole.INTERN to VeilederAutorisasjonService(veilarbPep, authContextHolder, metricsService),
            UserRole.SYSTEM to SystembrukerAutorisasjonService(authContextHolder, metricsService)
        )

        return TilgangskontrollService(authContextHolder, autorisasjonServiceMap)
    }
}