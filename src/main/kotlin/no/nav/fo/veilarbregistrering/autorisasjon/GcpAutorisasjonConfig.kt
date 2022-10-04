package no.nav.fo.veilarbregistrering.autorisasjon

import no.nav.common.abac.Pep
import no.nav.common.abac.VeilarbPepFactory
import no.nav.common.abac.audit.SpringAuditRequestInfoSupplier
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.UserRole
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("gcp")
class GcpAutorisasjonConfig {

    @Bean
    fun veilarbPep(): Pep {
        return VeilarbPepFactory.get(
            requireProperty("ABAC_PDP_ENDPOINT_URL"),
            requireProperty("SERVICEUSER_USERNAME"),
            requireProperty("SERVICEUSER_PASSWORD"),
            SpringAuditRequestInfoSupplier()
        )
    }

    @Bean
    fun tilgangskontrollService(
        veilarbPep: Pep,
        authContextHolder: AuthContextHolder,
        metricsService: MetricsService
    ): TilgangskontrollService {
        val autorisasjonServiceMap = mapOf(
            UserRole.EKSTERN to PersonbrukerAutorisasjonService(veilarbPep, authContextHolder, metricsService),
            UserRole.INTERN to VeilederAutorisasjonService(veilarbPep, authContextHolder, metricsService),
            UserRole.SYSTEM to SystembrukerAutorisasjonService(authContextHolder, metricsService)
        )

        return TilgangskontrollService(authContextHolder, autorisasjonServiceMap)
    }
}