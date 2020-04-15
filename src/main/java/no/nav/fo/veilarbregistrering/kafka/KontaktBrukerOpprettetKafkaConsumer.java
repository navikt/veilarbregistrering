package no.nav.fo.veilarbregistrering.kafka;

import no.nav.arbeid.soker.oppgave.KontaktBrukerOpprettetEvent;
import no.nav.common.utils.IdUtils;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.registrering.bruker.DatakvalitetOppholdstillatelseService;
import no.nav.log.MDCConstants;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
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

/**
 * 1. Den skal konsumere TOPIC for "Kontakt bruker opprettet"
 * 2. Den skal kjøre i evig løkka
 * 3. Den skal kalle på et internt API for å hente oppholdstillatelse
 */
class KontaktBrukerOpprettetKafkaConsumer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(KontaktBrukerOpprettetKafkaConsumer.class);

    private final Properties kafkaConsumerProperties;
    private final UnleashService unleashService;
    private final String topic;
    private final DatakvalitetOppholdstillatelseService bruker;

    KontaktBrukerOpprettetKafkaConsumer(
            Properties kafkaConsumerProperties,
            UnleashService unleashService,
            String topic,
            DatakvalitetOppholdstillatelseService bruker) {
        this.kafkaConsumerProperties = kafkaConsumerProperties;
        this.unleashService = unleashService;
        this.topic = topic;
        this.bruker = bruker;

        Executors.newSingleThreadScheduledExecutor()
                .schedule(this, 5, MINUTES);
    }

    @Override
    public void run() {
        LOG.info("Running");

        try(KafkaConsumer<String, KontaktBrukerOpprettetEvent> consumer = new KafkaConsumer<>(kafkaConsumerProperties)) {
            consumer.subscribe(Collections.singletonList(topic));

            while (konsumeringAvKontaktBruker()) {
                ConsumerRecords<String, KontaktBrukerOpprettetEvent> consumerRecords = consumer.poll(Duration.ofMinutes(2));
                LOG.info("Leser {} events fra topic {}}", consumerRecords.count(), topic);

                consumerRecords.forEach(record -> {
                    MDC.put(MDCConstants.MDC_CALL_ID, IdUtils.generateId());
                    LOG.info("Behandler kontaktBrukerOpprettetEvent");

                    KontaktBrukerOpprettetEvent kontaktBrukerOpprettetEvent = record.value();
                    bruker.hentOgSammenlignOppholdFor(AktorId.valueOf(kontaktBrukerOpprettetEvent.getAktorid()));
                });
                consumer.commitSync();
            }
        } catch (Exception e) {
            LOG.error(String.format("Det oppstod en ukjent feil ifm. konsumering av events fra %s", topic), e);
        }
    }

    private boolean konsumeringAvKontaktBruker() {
        return unleashService.isEnabled("veilarbregistrering.konsumeringAvKontaktBruker");
    }
}
