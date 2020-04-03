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

public class ArbeidssokerRegistrertKafkaProducer implements ArbeidssokerRegistrertProducer {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidssokerRegistrertKafkaProducer.class);

    private final KafkaProducer producer;
    private final UnleashService unleashService;
    private final String topic;

    public ArbeidssokerRegistrertKafkaProducer(
            KafkaProducer kafkaProducer, UnleashService unleashService, String topic) {
        this.unleashService = unleashService;
        this.producer = kafkaProducer;
        this.topic = topic;
    }

    @Override
    public void publiserArbeidssokerRegistrert(
            AktorId aktorId,
            DinSituasjonSvar brukersSituasjon,
            LocalDateTime opprettetDato) {

        if (!skalArbeidssokerRegistrertPubliseres()) {
            LOG.info("Feature toggle, arbeidssokerregistrering.arbeidssokerRegistrert, er skrudd av. Det publiseres ingen Kafka-event");
            return;
        }

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

    private boolean skalArbeidssokerRegistrertPubliseres() {
        return unleashService.isEnabled("arbeidssokerregistrering.arbeidssokerRegistrert");
    }
}
