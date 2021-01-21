package no.nav.fo.veilarbregistrering.config

import no.nav.fo.veilarbregistrering.log.loggerFor
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.flywaydb.core.Flyway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FlywayConfiguration {
    //    @Bean
    //    public FlywayConfigurationCustomizer flywayConfig(@Value("${spring.cloud.vault.database.role}") String role) {
    //        return c -> c.initSql(format("SET ROLE \"%s\"", role));
    //    }
    val log = loggerFor<FlywayConfiguration>()

    @Bean
    fun cleanMigrateStrategy(): FlywayMigrationStrategy {

        log.debug("Defining migration strategy")
        return FlywayMigrationStrategy { flyway: Flyway ->
            println("TJOHEI")
            flyway.repair()
            flyway.migrate()
        }
    }
}