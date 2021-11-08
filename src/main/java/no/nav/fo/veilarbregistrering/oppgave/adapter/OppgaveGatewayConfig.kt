package no.nav.fo.veilarbregistrering.oppgave.adapter

import no.nav.common.sts.ServiceToServiceTokenProvider
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OppgaveGatewayConfig {
    @Bean
    fun oppgaveRestClient(
        systemUserTokenProvider: SystemUserTokenProvider,
        tokenProvider: ServiceToServiceTokenProvider
    ): OppgaveRestClient {
        val cluster = EnvironmentUtils.getRequiredProperty(OPPGAVE_CLUSTER)
        val env = EnvironmentUtils.getRequiredProperty("APP_ENVIRONMENT_NAME")
        val serviceName = if (env == "p") "oppgave" else "oppgave-q1"

        return OppgaveRestClient(EnvironmentUtils.getRequiredProperty(OPPGAVE_PROPERTY_NAME), systemUserTokenProvider) {
            tokenProvider.getServiceToken(
                serviceName,
                "oppgavehandtering",
                cluster
            )
        }
    }

    @Bean
    fun oppgaveGateway(oppgaveRestClient: OppgaveRestClient): OppgaveGateway = OppgaveGatewayImpl(oppgaveRestClient)

    companion object {
        const val OPPGAVE_PROPERTY_NAME = "OPPGAVE_URL"
        const val OPPGAVE_CLUSTER = "OPPGAVE_CLUSTER"
    }
}