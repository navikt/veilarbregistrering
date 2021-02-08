package no.nav.fo.veilarbregistrering.helsesjekk

import no.nav.common.abac.Pep
import no.nav.common.featuretoggle.UnleashService
import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.health.selftest.SelfTestChecks
import no.nav.common.health.selftest.SelfTestMeterBinder
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlOppslagConfig.PDL_PROPERTY_NAME
import no.nav.fo.veilarbregistrering.helsesjekk.HealthCheck.performHealthCheck
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayConfig.OPPFOLGING_API_PROPERTY_NAME
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykemeldingGatewayConfig.INFOTRYGDAPI_URL_PROPERTY_NAME
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
class HelsesjekkConfig {
    @Bean
    fun selfTestChecks(
        jdbcTemplate: JdbcTemplate,
        veilarbPep: Pep,
        unleashService: UnleashService,
    ): SelfTestChecks {
        val oppFolgingPingUrl = "${getRequiredProperty(OPPFOLGING_API_PROPERTY_NAME)}/ping"
        val pdlPingUrl = getRequiredProperty(PDL_PROPERTY_NAME)
        val foInfotrygdPingUrl = "${INFOTRYGDAPI_URL_PROPERTY_NAME}/rest/internal/isAlive"

        val selfTestChecks = listOf(
            SelfTestCheck("Enkel spørring mot Databasen til veilarregistrering.", true, checkDbHealth(jdbcTemplate)),
            SelfTestCheck("ABAC tilgangskontroll - ping", true, veilarbPep.abacClient),
            SelfTestCheck("Sjekker at feature-toggles kan hentes fra Unleash", false, unleashService),
            SelfTestCheck("Ping Oppfølging", false, healthCheck(oppFolgingPingUrl)),
            //TODO: Sjekk om dette fikser SelfTestCheck("Ping Pdl", false, healthCheck(pdlPingUrl, true)),
            SelfTestCheck("Ping FO Infotrygd", false, healthCheck(foInfotrygdPingUrl)),
        )
        return SelfTestChecks(selfTestChecks)
    }

    @Bean
    fun selfTestMeterBinder(selfTestChecks: SelfTestChecks): SelfTestMeterBinder {
        return SelfTestMeterBinder(selfTestChecks)
    }

    private fun healthCheck(url: String, useOptions: Boolean = false): HealthCheck = HealthCheck {
        performHealthCheck(url, useOptions)
    }

    private fun checkDbHealth(jdbcTemplate: JdbcTemplate): HealthCheck {
        return HealthCheck {
            try {
                jdbcTemplate.queryForObject("SELECT 1 FROM DUAL", Long::class.java)
                HealthCheckResult.healthy()
            } catch (e: Exception) {
                HealthCheckResult.unhealthy("Fikk ikke kontakt med databasen", e)
            }
        }
    }
}