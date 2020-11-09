package no.nav.fo.veilarbregistrering.kafka;

import no.nav.arbeid.soker.registrering.ArbeidssokerRegistrertEvent;
import no.nav.fo.veilarbregistrering.registrering.bruker.ArbeidssokerRegistrertInternalEvent;
import no.nav.fo.veilarbregistrering.registrering.bruker.ArbeidssokerRegistrertProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.nav.fo.veilarbregistrering.kafka.ArbeidssokerRegistrertMapper.map;
import static no.nav.fo.veilarbregistrering.log.CallId.getCorrelationIdAsBytes;
import static no.nav.log.MDCConstants.MDC_CALL_ID;

class ArbeidssokerRegistrertKafkaProducer implements ArbeidssokerRegistrertProducer {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidssokerRegistrertKafkaProducer.class);

    private final KafkaProducer producer;
    private final String topic;

    ArbeidssokerRegistrertKafkaProducer(KafkaProducer kafkaProducer, String topic) {
        this.producer = kafkaProducer;
        this.topic = topic;
    }

    @Override
    public void publiserArbeidssokerRegistrert(
            ArbeidssokerRegistrertInternalEvent arbeidssokerRegistrertInternalEvent) {

        try {
            ArbeidssokerRegistrertEvent arbeidssokerRegistrertEvent = map(arbeidssokerRegistrertInternalEvent);
            ProducerRecord<String, ArbeidssokerRegistrertEvent> record = new ProducerRecord<>(topic, arbeidssokerRegistrertInternalEvent.getAktorId().asString(), arbeidssokerRegistrertEvent);
            record.headers().add(new RecordHeader(MDC_CALL_ID, getCorrelationIdAsBytes()));
            producer.send(record, (recordMetadata, e) -> {
                if (e != null) {
                    LOG.error(String.format("ArbeidssokerRegistrertEvent publisert på topic, %s", topic), e);

                } else {
                    LOG.info("ArbeidssokerRegistrertEvent publisert: {}", recordMetadata);
                }
            });

        } catch (Exception e) {
            LOG.error("Sending av arbeidssokerRegistrertEvent til Kafka feilet", e);
        }
    }
}
