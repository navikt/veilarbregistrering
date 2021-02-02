package no.nav.fo.veilarbregistrering.helsesjekk

import no.nav.common.health.selftest.SelfTestChecks
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HelsesjekkConfig {
    @Bean
    fun selfTestChecks(
    ): SelfTestChecks {
        return SelfTestChecks(emptyList())
    }
}