package no.nav.fo.veilarbregistrering.kafka

import io.mockk.mockk
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertProducer
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerProfilertProducer
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertProducerV2
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaConfig {
    @Bean
    fun arbeidssokerRegistrertKafkaProducer(): ArbeidssokerRegistrertProducer = mockk()

    @Bean
    fun arbeidssokerRegistrertKafkaProducerv2(): ArbeidssokerRegistrertProducerV2 = mockk()

    @Bean
    fun arbeidssokerProfilertKafkaProducer(): ArbeidssokerProfilertProducer = mockk()
}
