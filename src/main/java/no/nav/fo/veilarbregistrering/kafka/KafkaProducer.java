package no.nav.fo.veilarbregistrering.kafka;

import no.nav.arbeid.soker.oppgave.Henvendelse;
import no.nav.arbeid.soker.registrering.Registrering;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveSender;
import no.nav.fo.veilarbregistrering.registrering.bruker.ArbeidssokerregistreringSender;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.lang.System.getenv;

public class KafkaProducer implements ArbeidssokerregistreringSender, OppgaveSender {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaProducer.class);

    private final org.apache.kafka.clients.producer.KafkaProducer producer;
    private final org.apache.kafka.clients.producer.KafkaProducer henvendelseProducer;

    public KafkaProducer() {
        this.producer = new org.apache.kafka.clients.producer.KafkaProducer(KafkaConfig.getKafkaConfig());
        this.henvendelseProducer = new org.apache.kafka.clients.producer.KafkaProducer(KafkaConfig.getKafkaConfig());
    }

    @Override
    public void sendRegistreringsMelding(String aktorId) {
        Registrering registrering = Registrering.newBuilder().setAktorid(aktorId).build();
        try {
            producer.send(new ProducerRecord<>("aapen-arbeid-arbeidssoker-registrert" + getEnvSuffix(), aktorId, registrering)).get(2, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            LOG.warn("Sending av registrering til Kafka feilet", e);
        }
    }

    @Override
    public void sendMelding(String aktorId) {
        Henvendelse henvendelse = Henvendelse.newBuilder().setAktorid(aktorId).build();
        try {
            henvendelseProducer.send(new ProducerRecord<>("aapen-arbeid-arbeidssoker-henvendelse-opprettet-q0" + getEnvSuffix(), aktorId, henvendelse)).get(2, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            LOG.warn("Sending av henvendelse til Kafka feilet", e);
        }
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
