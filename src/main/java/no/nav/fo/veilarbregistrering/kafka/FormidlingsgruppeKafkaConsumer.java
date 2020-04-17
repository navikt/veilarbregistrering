package no.nav.fo.veilarbregistrering.kafka;

import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.MINUTES;
import static no.nav.log.MDCConstants.MDC_CALL_ID;

/**
 * 1. Den skal konsumere TOPIC for "Formidlingsgruppe"
 * 2. Den skal kjøre i evig løkka
 * 3. Den skal kalle på et internt API for å lagre formidlingsgruppe knyttet til person
 */
class FormidlingsgruppeKafkaConsumer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(FormidlingsgruppeKafkaConsumer.class);

    private final Properties kafkaConsumerProperties;
    private final UnleashService unleashService;
    private final String topic;

    FormidlingsgruppeKafkaConsumer(
            Properties kafkaConsumerProperties,
            UnleashService unleashService,
            String topic) {
        this.kafkaConsumerProperties = kafkaConsumerProperties;
        this.unleashService = unleashService;
        this.topic = topic;

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
                LOG.info("Leser {} events fra topic {}}", consumerRecords.count(), topic);

                consumerRecords.forEach(record -> {
                    Header header = record.headers().lastHeader(MDC_CALL_ID);
                    String callId = new String(header.value(), StandardCharsets.UTF_8);
                    MDC.put(MDC_CALL_ID, callId);

                    //String key = record.key();
                    String formidlingsgruppeEvent = record.value();

                    LOG.info("Behandler kontaktBrukerOpprettetEvent: {}", formidlingsgruppeEvent);

                    MDC.remove(MDC_CALL_ID);
                });
                //consumer.commitSync();
            }
        } catch (Exception e) {
            LOG.error(String.format("Det oppstod en ukjent feil ifm. konsumering av events fra %s", topic), e);
        }
    }

    private boolean konsumeringAvFormidlingsgruppe() {
        return unleashService.isEnabled("veilarbregistrering.konsumeringAvFormidlingsgruppe");
    }
}
