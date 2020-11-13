package no.nav.fo.veilarbregistrering.kafka;

import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerService;
import no.nav.fo.veilarbregistrering.kafka.formidlingsgruppe.FormidlingsgruppeMapper;
import no.nav.fo.veilarbregistrering.log.CallId;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.MINUTES;
import static no.nav.log.MDCConstants.MDC_CALL_ID;

/**
 * 1. Den skal konsumere TOPIC for "Formidlingsgruppe" fra Arena
 * 2. Den skal kjøre i evig løkka
 * 3. Den skal kalle på et internt API for å lagre formidlingsgruppe knyttet til person
 */
class FormidlingsgruppeKafkaConsumer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(FormidlingsgruppeKafkaConsumer.class);

    private final Properties kafkaConsumerProperties;
    private final String topic;
    private final ArbeidssokerService arbeidssokerService;
    private final UnleashService unleashService;


    FormidlingsgruppeKafkaConsumer(
            Properties kafkaConsumerProperties,
            String topic,
            ArbeidssokerService arbeidssokerService,
            UnleashService unleashService) {
        this.kafkaConsumerProperties = kafkaConsumerProperties;
        this.topic = topic;
        this.arbeidssokerService = arbeidssokerService;
        this.unleashService = unleashService;

        Executors.newSingleThreadScheduledExecutor()
                .schedule(this, 5, MINUTES);
    }

    @Override
    public void run() {

        LOG.info("Running");

        try(KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaConsumerProperties)) {
            consumer.subscribe(Collections.singletonList(topic));

            LOG.info("Subscribing to {}", topic);

            while (konsumeringAvFormidlingsgruppe()) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMinutes(2));
                LOG.info("Leser {} events fra topic {}", consumerRecords.count(), topic);

                consumerRecords.forEach(record -> {
                    CallId.leggTilCallId();

                    try {
                        behandleRecord(record);
                    } catch (RuntimeException e) {
                        LOG.error(String.format("Behandling av formidlingsgruppeEvent feilet: %s", record.value()), e);
                        throw e;
                    }

                    MDC.remove(MDC_CALL_ID);
                });
                consumer.commitSync();
            }

        } catch (Exception e) {
            LOG.error(String.format("Det oppstod en ukjent feil ifm. konsumering av events fra %s", topic), e);
            MDC.remove(MDC_CALL_ID);
        }
    }

    void behandleRecord(ConsumerRecord<String, String> record) {
        FormidlingsgruppeEvent formidlingsgruppeEvent = FormidlingsgruppeMapper.map(record.value());
        arbeidssokerService.behandle(formidlingsgruppeEvent);
    }

    private boolean konsumeringAvFormidlingsgruppe() {
        return unleashService.isEnabled("veilarbregistrering.konsumeringAvFormidlingsgruppe");
    }
}
