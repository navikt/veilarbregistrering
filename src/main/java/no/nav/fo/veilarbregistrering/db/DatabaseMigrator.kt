package no.nav.fo.veilarbregistrering.db

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct
import javax.sql.DataSource

@Configuration
class DatabaseMigrator @Autowired constructor(private val dataSource: DataSource) {
    @PostConstruct
    fun migrateDb() {
        val flyway = Flyway()
        flyway.dataSource = dataSource
        flyway.migrate()
    }
}