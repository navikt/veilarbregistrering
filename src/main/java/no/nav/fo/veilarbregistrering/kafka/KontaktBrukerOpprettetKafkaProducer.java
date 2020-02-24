package no.nav.fo.veilarbregistrering.kafka;

import no.nav.arbeid.soker.oppgave.KontaktBrukerOpprettetEvent;
import no.nav.fo.veilarbregistrering.oppgave.KontaktBrukerHenvendelseProducer;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.lang.System.getenv;

public class KontaktBrukerOpprettetKafkaProducer implements KontaktBrukerHenvendelseProducer {

    private static final Logger LOG = LoggerFactory.getLogger(KontaktBrukerOpprettetKafkaProducer.class);

    private final org.apache.kafka.clients.producer.KafkaProducer producer;
    private final UnleashService unleashService;

    public KontaktBrukerOpprettetKafkaProducer(org.apache.kafka.clients.producer.KafkaProducer kafkaProducer, UnleashService unleashService) {
        this.unleashService = unleashService;
        this.producer = kafkaProducer;
    }

    @Override
    public void publiserHenvendelse(AktorId aktorId) {
        if (!skalKontaktBrukerHenvendelsePubliseres()) {
            LOG.info("Feature toggle, arbeidssokerregistrering.kontantBrukerHenvendelse, er skrudd av. Det publiseres ingen Kafka-event");
            return;
        }

        KontaktBrukerOpprettetEvent kontaktBrukerOpprettetEvent = KontaktBrukerOpprettetEvent.newBuilder().setAktorid(aktorId.asString()).build();
        try {
            producer.send(new ProducerRecord<>("aapen-arbeid-arbeidssoker-kontaktbruker-opprettet" + getEnvSuffix(), aktorId.asString(), kontaktBrukerOpprettetEvent)).get(2, TimeUnit.SECONDS);
            LOG.info("KontaktBrukerOpprettetEvent publisert på topic, aapen-arbeid-arbeidssoker-kontaktbruker-opprettet" + getEnvSuffix());

        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            LOG.warn("Sending av KontaktBrukerOpprettetEvent til Kafka feilet", e);

        } catch (Exception e) {
            LOG.error("Sending av arbeidssokerRegistrertEvent til Kafka feilet", e);
        }
    }

    private boolean skalKontaktBrukerHenvendelsePubliseres() {
        return unleashService.isEnabled("arbeidssokerregistrering.kontantBrukerHenvendelse");
    }

    private static String getEnvSuffix() {
        String envName = getenv("APP_ENVIRONMENT_NAME");
        if (envName != null) {
            return "-" + envName.toLowerCase();
        } else {
            return "";
        }
    }

}
