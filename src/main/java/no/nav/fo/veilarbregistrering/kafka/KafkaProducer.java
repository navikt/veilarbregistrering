package no.nav.fo.veilarbregistrering.kafka;

import no.nav.arbeid.soker.oppgave.KontaktBrukerOpprettetEvent;
import no.nav.arbeid.soker.registrering.ArbeidssokerRegistrertEvent;
import no.nav.fo.veilarbregistrering.oppgave.KontaktBrukerHenvendelseProducer;
import no.nav.fo.veilarbregistrering.registrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.registrering.bruker.ArbeidssokerRegistrertProducer;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.lang.System.getenv;

public class KafkaProducer implements ArbeidssokerRegistrertProducer, KontaktBrukerHenvendelseProducer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaProducer.class);

    private final org.apache.kafka.clients.producer.KafkaProducer producer;
    private final org.apache.kafka.clients.producer.KafkaProducer henvendelseProducer;

    private final UnleashService unleashService;

    public KafkaProducer(Properties kafkaProperties, UnleashService unleashService) {
        this.unleashService = unleashService;
        this.producer = new org.apache.kafka.clients.producer.KafkaProducer(kafkaProperties);
        this.henvendelseProducer = new org.apache.kafka.clients.producer.KafkaProducer(kafkaProperties);
    }

    @Override
    public void publiserArbeidssokerRegistrert(AktorId aktorId) {
        if (!skalArbeidssokerRegistrertPubliseres()) {
            LOG.info("Feature toggle, arbeidssokerregistrering.arbeidssokerRegistrert, er skrudd av. Det publiseres ingen Kafka-event");
            return;
        }

        ArbeidssokerRegistrertEvent arbeidssokerRegistrertEvent = ArbeidssokerRegistrertEvent.newBuilder().setAktorid(aktorId.asString()).build();
        try {
            producer.send(new ProducerRecord<>("aapen-arbeid-arbeidssoker-registrert" + getEnvSuffix(), aktorId.asString(), arbeidssokerRegistrertEvent)).get(2, TimeUnit.SECONDS);
            LOG.info("Arbeidssoker registrert-event publisert på topic, aapen-arbeid-arbeidssoker-registrert" + getEnvSuffix());

        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            LOG.warn("Sending av arbeidssokerRegistrertEvent til Kafka feilet", e);
        }
    }

    private boolean skalArbeidssokerRegistrertPubliseres() {
        return unleashService.isEnabled("arbeidssokerregistrering.arbeidssokerRegistrert");
    }

    @Override
    public void publiserHenvendelse(AktorId aktorId) {
        if (!skalKontaktBrukerHenvendelsePubliseres()) {
            LOG.info("Feature toggle, arbeidssokerregistrering.kontantBrukerHenvendelse, er skrudd av. Det publiseres ingen Kafka-event");
            return;
        }

        KontaktBrukerOpprettetEvent kontaktBrukerOpprettetEvent = KontaktBrukerOpprettetEvent.newBuilder().setAktorid(aktorId.asString()).build();
        try {
            henvendelseProducer.send(new ProducerRecord<>("aapen-arbeid-arbeidssoker-kontaktbruker-opprettet" + getEnvSuffix(), aktorId.asString(), kontaktBrukerOpprettetEvent)).get(2, TimeUnit.SECONDS);
            LOG.info("KontaktBrukerOpprettetEvent publisert på topic, aapen-arbeid-arbeidssoker-kontaktbruker-opprettet" + getEnvSuffix());

        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            LOG.warn("Sending av KontaktBrukerOpprettetEvent til Kafka feilet", e);
        }
    }

    private boolean skalKontaktBrukerHenvendelsePubliseres() {
        return unleashService.isEnabled("arbeidssokerregistrering.kontantBrukerHenvendelse");
    }

    private static String getEnvSuffix() {
        String envName = getenv("APP_ENVIRONMENT_NAME");
        if (envName != null) {
            return "-" + envName.toLowerCase();
        } else {
            return "";
        }
    }

}
