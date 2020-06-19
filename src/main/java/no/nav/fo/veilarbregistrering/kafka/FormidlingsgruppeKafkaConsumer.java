package no.nav.fo.veilarbregistrering.kafka;

import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerService;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * 1. Den skal konsumere TOPIC for "Formidlingsgruppe" fra Arena
 * 2. Den skal kjøre i evig løkka
 * 3. Den skal kalle på et internt API for å lagre formidlingsgruppe knyttet til person
 */
class FormidlingsgruppeKafkaConsumer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(FormidlingsgruppeKafkaConsumer.class);

    private final Properties kafkaConsumerProperties;
    private final UnleashService unleashService;
    private final String topic;
    private final ArbeidssokerService arbeidssokerService;

    FormidlingsgruppeKafkaConsumer(
            Properties kafkaConsumerProperties,
            UnleashService unleashService,
            String topic, ArbeidssokerService arbeidssokerService) {
        this.kafkaConsumerProperties = kafkaConsumerProperties;
        this.unleashService = unleashService;
        this.topic = topic;
        this.arbeidssokerService = arbeidssokerService;

        Executors.newSingleThreadScheduledExecutor()
                .schedule(this, 5, MINUTES);
    }

    @Override
    public void run() {
        LOG.info("Running");

        try(KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaConsumerProperties)) {
            consumer.subscribe(Collections.singletonList(topic));

            while (konsumeringAvFormidlingsgruppe()) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMinutes(2));
                LOG.info("Leser {} events fra topic {}", consumerRecords.count(), topic);

                consumerRecords.forEach(record -> {
                    behandleRecord(record);
                });
                consumer.commitSync();
            }
        } catch (Exception e) {
            LOG.error(String.format("Det oppstod en ukjent feil ifm. konsumering av events fra %s", topic), e);
        }
    }

    void behandleRecord(ConsumerRecord<String, String> record) {
        LOG.info("Behandler rådata fra {}: {}", topic, record);
        FormidlingsgruppeEvent formidlingsgruppeEvent = FormidlingsgruppeMapper.map(record.value());

        arbeidssokerService.behandle(formidlingsgruppeEvent);
    }

    private boolean konsumeringAvFormidlingsgruppe() {
        return unleashService.isEnabled("veilarbregistrering.konsumeringAvFormidlingsgruppe");
    }
}
