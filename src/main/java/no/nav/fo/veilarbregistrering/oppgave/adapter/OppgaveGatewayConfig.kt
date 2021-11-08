package no.nav.fo.veilarbregistrering.oppgave.adapter

import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OppgaveGatewayConfig {
    @Bean
    fun oppgaveRestClient(systemUserTokenProvider: SystemUserTokenProvider?): OppgaveRestClient =
        OppgaveRestClient(EnvironmentUtils.getRequiredProperty(OPPGAVE_PROPERTY_NAME), systemUserTokenProvider!!)

    @Bean
    fun oppgaveGateway(oppgaveRestClient: OppgaveRestClient?): OppgaveGateway = OppgaveGatewayImpl(oppgaveRestClient!!)

    companion object {
        const val OPPGAVE_PROPERTY_NAME = "OPPGAVE_URL"
    }
}