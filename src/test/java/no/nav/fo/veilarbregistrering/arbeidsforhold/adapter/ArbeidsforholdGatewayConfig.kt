package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ArbeidsforholdGatewayConfig {

    @Bean
    fun arbeidsforholdGateway() = ArbeidsforholdGatewayImpl(StubAaregRestClient())
}
