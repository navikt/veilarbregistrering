package no.nav.fo.veilarbregistrering.helsesjekk

import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.health.selftest.SelfTestChecks
import no.nav.common.health.selftest.SelfTestMeterBinder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
class HelsesjekkConfig {
    @Bean
    fun selfTestChecks(
        jdbcTemplate: JdbcTemplate,
    ): SelfTestChecks {
        return SelfTestChecks(listOf(
            SelfTestCheck("Databasesjekk", true, checkDbHealth(jdbcTemplate)),
            SelfTestCheck("Dummysjekk", false, checkDummy())
        ))
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

    private fun checkDummy() = HealthCheck { HealthCheckResult.healthy() }

    @Bean
    fun selfTestAggregateMeterBinder(selfTestChecks: SelfTestChecks) = SelfTestMeterBinder(selfTestChecks)

    @Bean
    fun selfTestStatusMeterBinder(selfTestChecks: SelfTestChecks) = SelfTestStatusMeterBinder(selfTestChecks)
}
