package no.nav.fo.veilarbregistrering.db

import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import org.springframework.jdbc.core.JdbcTemplate


class DatabaseHelsesjekk(var jdbcTemplate: JdbcTemplate) : HealthCheck {
    override fun checkHealth(): HealthCheckResult {
        try {
            jdbcTemplate.queryForObject("SELECT 1 FROM DUAL", Long::class.java)
            return HealthCheckResult.healthy()
        } catch (e: Exception) {
            return HealthCheckResult.unhealthy("Fikk ikke kontakt med databasen", e)
        }
    }
}