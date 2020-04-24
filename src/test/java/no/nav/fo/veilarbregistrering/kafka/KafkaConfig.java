package no.nav.fo.veilarbregistrering.kafka;

import no.nav.fo.veilarbregistrering.oppgave.KontaktBrukerHenvendelseProducer;
import no.nav.fo.veilarbregistrering.registrering.bruker.ArbeidssokerRegistrertProducer;
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
    KontaktBrukerHenvendelseProducer kontaktBrukerOpprettetKafkaProducer() {
        return mock(KontaktBrukerHenvendelseProducer.class);
    }
}

