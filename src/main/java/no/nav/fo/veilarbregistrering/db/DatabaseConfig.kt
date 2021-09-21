package no.nav.fo.veilarbregistrering.db

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@Import(
    DatabaseMigrator::class
)
class DatabaseConfig {
    @Bean(name = ["transactionManager"])
    fun transactionManager(dataSource: DataSource): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

    @Bean
    fun jdbcTemplate(dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource)
    }

    @Bean
    fun namedParameterJdbcTemplate(dataSource: DataSource): NamedParameterJdbcTemplate {
        return NamedParameterJdbcTemplate(dataSource)
    }

    @Bean
    fun databaseHelsesjekk(jdbcTemplate: JdbcTemplate): DatabaseHelsesjekk {
        return DatabaseHelsesjekk(jdbcTemplate)
    }

    companion object {
        const val VEILARBREGISTRERINGDB_URL = "VEILARBREGISTRERINGDB_URL"
        const val VEILARBREGISTRERINGDB_USERNAME = "VEILARBREGISTRERINGDB_USERNAME"
        const val VEILARBREGISTRERINGDB_PASSWORD = "VEILARBREGISTRERINGDB_PASSWORD"
    }
}