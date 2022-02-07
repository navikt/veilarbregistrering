package no.nav.fo.veilarbregistrering.helsesjekk

import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.health.selftest.SelfTestChecks
import no.nav.common.health.selftest.SelfTestMeterBinder
import no.nav.fo.veilarbregistrering.db.DatabaseHelsesjekk
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
            SelfTestCheck("Databasesjekk", true, DatabaseHelsesjekk(jdbcTemplate)),
            SelfTestCheck("Dummysjekk", false, checkDummy())
        ))
    }

    private fun checkDummy() = HealthCheck { HealthCheckResult.healthy() }

    @Bean
    fun selfTestAggregateMeterBinder(selfTestChecks: SelfTestChecks) = SelfTestMeterBinder(selfTestChecks)

    @Bean
    fun selfTestStatusMeterBinder(selfTestChecks: SelfTestChecks) = SelfTestStatusMeterBinder(selfTestChecks)
}
