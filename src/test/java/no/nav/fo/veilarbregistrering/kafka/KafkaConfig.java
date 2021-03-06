package no.nav.fo.veilarbregistrering.kafka;

import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerProfilertProducer;
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class KafkaConfig {

    @Bean
    ArbeidssokerRegistrertProducer arbeidssokerRegistrertKafkaProducer() {
        return mock(ArbeidssokerRegistrertProducer.class);
    }

    @Bean
    ArbeidssokerProfilertProducer arbeidssokerProfilertProducer() {
        return mock(ArbeidssokerProfilertProducer.class);
    }
}

