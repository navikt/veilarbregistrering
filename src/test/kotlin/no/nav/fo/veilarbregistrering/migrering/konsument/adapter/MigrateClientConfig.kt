package no.nav.fo.veilarbregistrering.migrering.konsument.adapter

import io.mockk.mockk
import no.nav.fo.veilarbregistrering.migrering.konsument.MigrateClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MigrateClientConfig {

    @Bean
    fun migrateClient(): MigrateClient {
        return mockk()
    }
}