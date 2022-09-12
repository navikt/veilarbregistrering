package no.nav.fo.veilarbregistrering.oppgave.adapter

import no.nav.common.token_client.client.AzureAdMachineToMachineTokenClient
import no.nav.fo.veilarbregistrering.config.isProduction
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OppgaveGatewayConfig {
    @Bean
    fun oppgaveRestClient(
        metricsService: MetricsService,
        tokenProvider: AzureAdMachineToMachineTokenClient
    ): OppgaveRestClient {
        val cluster = requireProperty(OPPGAVE_CLUSTER)
        val serviceName = if (isProduction()) "oppgave" else "oppgave-q1"

        return OppgaveRestClient(requireProperty(OPPGAVE_PROPERTY_NAME), metricsService) {
            tokenProvider.createMachineToMachineToken("api://$cluster.oppgavehandtering.$serviceName/.default")
        }
    }

    @Bean
    fun oppgaveGateway(oppgaveRestClient: OppgaveRestClient): OppgaveGateway = OppgaveGatewayImpl(oppgaveRestClient)

    companion object {
        const val OPPGAVE_PROPERTY_NAME = "OPPGAVE_URL"
        const val OPPGAVE_CLUSTER = "OPPGAVE_CLUSTER"
    }
}