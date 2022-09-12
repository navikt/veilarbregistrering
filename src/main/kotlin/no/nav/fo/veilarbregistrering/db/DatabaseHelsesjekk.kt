package no.nav.fo.veilarbregistrering.db

import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.fo.veilarbregistrering.config.isOnPrem
import org.springframework.jdbc.core.JdbcTemplate


class DatabaseHelsesjekk(var jdbcTemplate: JdbcTemplate) : HealthCheck {
    override fun checkHealth(): HealthCheckResult {
        val pingQuery = if (isOnPrem()) "SELECT 1 FROM DUAL" else "SELECT 1"
        return try {
            jdbcTemplate.queryForObject(pingQuery, Long::class.java)
            HealthCheckResult.healthy()
        } catch (e: Exception) {
            HealthCheckResult.unhealthy("Fikk ikke kontakt med databasen", e)
        }
    }
}