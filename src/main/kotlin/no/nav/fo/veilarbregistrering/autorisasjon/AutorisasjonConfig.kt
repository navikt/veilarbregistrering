package no.nav.fo.veilarbregistrering.autorisasjon

import no.nav.common.abac.*
import no.nav.common.abac.audit.AuditConfig
import no.nav.common.abac.audit.NimbusSubjectProvider
import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.UserRole
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AutorisasjonConfig {

    @Bean
    fun pep(abacClient: AbacClient): Pep {
        return VeilarbPep(
            requireProperty("SERVICEUSER_USERNAME"),
            abacClient,
            NimbusSubjectProvider(),
            AuditConfig(null, null, null)
        )
    }

    @Bean
    fun abacClient(
        machineToMachineTokenClient: MachineToMachineTokenClient
    ): AbacClient {
        val client = AbacHttpClient(requireProperty("ABAC_PDP_ENDPOINT_URL"))
        {
            val abacCluster = requireProperty("ABAC_CLUSTER")
            "Bearer " + machineToMachineTokenClient.createMachineToMachineToken("api://$abacCluster.pto.abac-veilarb-proxy/.default")
        }

        return AbacCachedClient(client)
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