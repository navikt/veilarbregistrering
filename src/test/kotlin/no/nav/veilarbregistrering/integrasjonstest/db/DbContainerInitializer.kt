package no.nav.veilarbregistrering.integrasjonstest.db

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy

class DbContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        postgres.start()
        TestPropertyValues.of(
            "spring.datasource.url=${postgres.jdbcUrl}",
            "spring.datasource.username=${postgres.username}",
            "spring.datasource.password=${postgres.password}"
        ).applyTo(applicationContext.environment)
    }

    companion object {

        // Lazy because we only want it to be initialized when accessed
        private val postgres: KPostgreSQLContainer by lazy {
            KPostgreSQLContainer("postgres:12.1")
                .withDatabaseName("veilarbregistrering").waitingFor(HostPortWaitStrategy())
                .withExposedPorts(5432)
                .withUsername("postgres")
                .withPassword("test")
        }
    }
}

// Hack needed because testcontainers use of generics confuses Kotlin
class KPostgreSQLContainer(imageName: String) : PostgreSQLContainer<KPostgreSQLContainer>(imageName)