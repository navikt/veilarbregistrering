package no.nav.fo.veilarbregistrering.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class KafkaConfig {

    @Bean
    ArbeidssokerRegistrertKafkaProducer arbeidssokerRegistrertKafkaProducer() {
        return mock(ArbeidssokerRegistrertKafkaProducer.class);
    }

    @Bean
    KontaktBrukerOpprettetKafkaProducer kontaktBrukerOpprettetKafkaProducer() {
        return mock(KontaktBrukerOpprettetKafkaProducer.class);
    }
}

