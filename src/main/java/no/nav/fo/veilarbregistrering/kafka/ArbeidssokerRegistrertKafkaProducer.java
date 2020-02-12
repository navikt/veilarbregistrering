package no.nav.fo.veilarbregistrering.kafka;

import no.nav.arbeid.soker.registrering.ArbeidssokerRegistrertEvent;
import no.nav.fo.veilarbregistrering.registrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.registrering.bruker.ArbeidssokerRegistrertProducer;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.lang.System.getenv;

public class ArbeidssokerRegistrertKafkaProducer implements ArbeidssokerRegistrertProducer {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidssokerRegistrertKafkaProducer.class);

    private final org.apache.kafka.clients.producer.KafkaProducer producer;
    private final UnleashService unleashService;

    public ArbeidssokerRegistrertKafkaProducer(org.apache.kafka.clients.producer.KafkaProducer kafkaProducer, UnleashService unleashService) {
        this.unleashService = unleashService;
        this.producer = kafkaProducer;
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

        } catch (Exception e) {
            LOG.error("Sending av arbeidssokerRegistrertEvent til Kafka feilet", e);
        }
    }

    private boolean skalArbeidssokerRegistrertPubliseres() {
        return unleashService.isEnabled("arbeidssokerregistrering.arbeidssokerRegistrert");
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
