package no.nav.fo.veilarbregistrering.kafka;

import no.nav.arbeid.soker.registrering.ArbeidssokerRegistrertEvent;
import no.nav.fo.veilarbregistrering.besvarelse.DinSituasjonSvar;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.registrering.bruker.ArbeidssokerRegistrertProducer;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static no.nav.fo.veilarbregistrering.kafka.ArbeidssokerRegistrertMapper.map;

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
            AktorId aktorId,
            DinSituasjonSvar brukersSituasjon,
            LocalDateTime opprettetDato) {

        try {
            ArbeidssokerRegistrertEvent arbeidssokerRegistrertEvent = map(aktorId, brukersSituasjon, opprettetDato);
            producer.send(new ProducerRecord<>(topic, aktorId.asString(), arbeidssokerRegistrertEvent)).get(2, TimeUnit.SECONDS);
            LOG.trace("Arbeidssoker registrert-event publisert på topic, {}", topic);

        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            LOG.warn("Sending av arbeidssokerRegistrertEvent til Kafka feilet", e);

        } catch (Exception e) {
            LOG.error("Sending av arbeidssokerRegistrertEvent til Kafka feilet", e);
        }
    }
}
