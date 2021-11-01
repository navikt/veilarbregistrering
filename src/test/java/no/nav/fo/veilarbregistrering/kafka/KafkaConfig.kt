package no.nav.fo.veilarbregistrering.kafka

import io.mockk.mockk
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertProducer
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerProfilertProducer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaConfig {
    @Bean
    fun arbeidssokerRegistrertKafkaProducer(): ArbeidssokerRegistrertProducer = mockk()

    @Bean
    fun arbeidssokerRegistrertKafkaProducerAiven(): ArbeidssokerRegistrertProducer = mockk()

    @Bean
    fun arbeidssokerProfilertKafkaProducer(): ArbeidssokerProfilertProducer = mockk()

    @Bean
    fun arbeidssokerProfilertKafkaProducerAiven(): ArbeidssokerProfilertProducer = mockk()

}
