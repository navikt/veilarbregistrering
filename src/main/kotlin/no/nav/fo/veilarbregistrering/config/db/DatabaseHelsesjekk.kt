package no.nav.fo.veilarbregistrering.config.db

import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import org.springframework.jdbc.core.JdbcTemplate


class DatabaseHelsesjekk(var jdbcTemplate: JdbcTemplate) : HealthCheck {
    override fun checkHealth(): HealthCheckResult {
        val pingQuery = "SELECT 1"
        return try {
            jdbcTemplate.queryForObject(pingQuery, Long::class.java)
            HealthCheckResult.healthy()
        } catch (e: Exception) {
            HealthCheckResult.unhealthy("Fikk ikke kontakt med databasen", e)
        }
    }
}