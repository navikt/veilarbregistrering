package no.nav.fo.veilarbregistrering.kafka;

import no.nav.arbeid.soker.oppgave.KontaktBrukerOpprettetEvent;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.registrering.bruker.DatakvalitetOppholdstillatelseService;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * 1. Den skal konsumere TOPIC for "Kontakt bruker opprettet"
 * 2. Den skal kjøre i evig løkka
 * 3. Den skal kalle på et internt API for å hente oppholdstillatelse
 */
public class KontaktBrukerOpprettetKafkaConsumer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(KontaktBrukerOpprettetKafkaConsumer.class);

    private final KafkaConsumer<String, KontaktBrukerOpprettetEvent> consumer;
    private final UnleashService unleashService;
    private final DatakvalitetOppholdstillatelseService bruker;

    public KontaktBrukerOpprettetKafkaConsumer(
            KafkaConsumer<String, KontaktBrukerOpprettetEvent> consumer,
            UnleashService unleashService,
            DatakvalitetOppholdstillatelseService bruker) {
        this.consumer = consumer;
        this.unleashService = unleashService;
        this.bruker = bruker;

        Executors.newSingleThreadScheduledExecutor()
                .schedule(this, 5, MINUTES);
    }

    @Override
    public void run() {
        LOG.info("Running");

        while (konsumeringAvKontaktBruker()) {
            ConsumerRecords<String, KontaktBrukerOpprettetEvent> consumerRecords = consumer.poll(Duration.ofMinutes(2));
            consumerRecords.forEach(record -> {
                LOG.info("Behandler kontaktBrukerOpprettetEvent");

                KontaktBrukerOpprettetEvent kontaktBrukerOpprettetEvent = record.value();
                bruker.hentOgSammenlignOppholdFor(AktorId.valueOf(kontaktBrukerOpprettetEvent.getAktorid()));
                //TODO: Skal dette gjøres pr. record, eller pr. poll?
                consumer.commitSync();
            });
        }
    }

    private boolean konsumeringAvKontaktBruker() {
        return unleashService.isEnabled("veilarbregistrering.konsumeringAvKontaktBruker");
    }
}
