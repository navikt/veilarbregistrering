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
            producer.send(record, (recordMetadata, e) -> {
               if (e != null) {
                   LOG.error(String.format("KontaktBrukerOpprettetEvent publisert på topic, %s", topic), e);

               } else {
                   LOG.info("KontaktBrukerOpprettetEvent publisert på topic, {}. RecordMetaData {}", topic, recordMetadata);
               }
            });

        } catch (Exception e) {
            LOG.error("Sending av arbeidssokerRegistrertEvent til Kafka feilet", e);
        }
    }
}
