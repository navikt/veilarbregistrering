package no.nav.fo.veilarbregistrering.postgres.migrering

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
class MigrationConfig {

    @Bean
    fun migreringPostgresStatusResource(migreringStatusService: MigrationStatusService): StatusResource {
        return StatusResource(migreringStatusService)
    }

    @Bean
    fun migreringStatusService(migrateRepository: MigrateRepository, migrateClient: MigrateClient): MigrationStatusService {
        return MigrationStatusService(migrateClient, migrateRepository)
    }

    @Bean
    fun migrateClient(): MigrateClient {
        return MigrateClient()
    }
}