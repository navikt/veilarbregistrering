package no.nav.fo.veilarbregistrering.migrering.konsument.scheduler

import io.mockk.mockk
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
class MigrateWorkerConfig {

    @Bean
    fun migrateWorker(): MigrateWorker = mockk(relaxed = true)
}