package no.nav.fo.veilarbregistrering.kafka;

import no.nav.arbeid.soker.oppgave.KontaktBrukerOpprettetEvent;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.oppgave.KontaktBrukerHenvendelseProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static no.nav.log.MDCConstants.MDC_CALL_ID;

class KontaktBrukerOpprettetKafkaProducer implements KontaktBrukerHenvendelseProducer {

    private static final Logger LOG = LoggerFactory.getLogger(KontaktBrukerOpprettetKafkaProducer.class);

    private final KafkaProducer producer;
    private final String topic;

    KontaktBrukerOpprettetKafkaProducer(KafkaProducer kafkaProducer, String topic) {
        this.producer = kafkaProducer;
        this.topic = topic;
    }

    @Override
    public void publiserHenvendelse(AktorId aktorId) {
        KontaktBrukerOpprettetEvent kontaktBrukerOpprettetEvent = KontaktBrukerOpprettetEvent.newBuilder().setAktorid(aktorId.asString()).build();
        try {
            ProducerRecord<String, KontaktBrukerOpprettetEvent> record = new ProducerRecord<>(topic, aktorId.asString(), kontaktBrukerOpprettetEvent);
            record.headers().add(new RecordHeader(MDC_CALL_ID, MDC.get(MDC_CALL_ID).getBytes(StandardCharsets.UTF_8)));
            producer.send(record).get(2, TimeUnit.SECONDS);
            LOG.info("KontaktBrukerOpprettetEvent publisert på topic, {}", topic);

        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            LOG.warn("Sending av KontaktBrukerOpprettetEvent til Kafka feilet", e);

        } catch (Exception e) {
            LOG.error("Sending av arbeidssokerRegistrertEvent til Kafka feilet", e);
        }
    }
}
