package no.nav.fo.veilarbregistrering.arbeidssoker.adapter

import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.arbeidssoker.FormidlingsgruppeGateway
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FormidlingsgruppeGatewayConfig {
    @Bean
    fun arenaOrdsTokenProviderClient(): ArenaOrdsTokenProviderClient =
        ArenaOrdsTokenProviderClient(requireProperty(ARENA_ORDS_TOKEN_PROVIDER))

    @Bean
    fun formidlingsgruppeRestClient(
        arenaOrdsTokenProviderClient: ArenaOrdsTokenProviderClient,
        metricsService: MetricsService
    ) =
        FormidlingsgruppeRestClient(
            requireProperty(ARENA_ORDS_API),
            metricsService,
        ) { arenaOrdsTokenProviderClient.token }

    @Bean
    fun formidlingsgruppeGateway(
        formidlingsgruppeRestClient: FormidlingsgruppeRestClient,
        unleashClient: UnleashClient
    ): FormidlingsgruppeGateway {
        return FormidlingsgruppeGatewayImpl(formidlingsgruppeRestClient, unleashClient)
    }

    companion object {
        private const val ARENA_ORDS_TOKEN_PROVIDER = "ARENA_ORDS_TOKEN_PROVIDER"
        private const val ARENA_ORDS_API = "ARENA_ORDS_API"
    }
}