package no.nav.fo.veilarbregistrering.kafka;

import no.nav.arbeid.soker.oppgave.Henvendelse;
import no.nav.arbeid.soker.registrering.Registrering;
import no.nav.fo.veilarbregistrering.oppgave.KontaktBrukerHenvendelseProducer;
import no.nav.fo.veilarbregistrering.registrering.bruker.ArbeidssokerRegistrertProducer;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.lang.System.getenv;

public class KafkaProducer implements ArbeidssokerRegistrertProducer, KontaktBrukerHenvendelseProducer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaProducer.class);

    private final org.apache.kafka.clients.producer.KafkaProducer producer;
    private final org.apache.kafka.clients.producer.KafkaProducer henvendelseProducer;

    private final UnleashService unleashService;

    public KafkaProducer(UnleashService unleashService) {
        this.unleashService = unleashService;
        this.producer = new org.apache.kafka.clients.producer.KafkaProducer(KafkaConfig.getKafkaConfig());
        this.henvendelseProducer = new org.apache.kafka.clients.producer.KafkaProducer(KafkaConfig.getKafkaConfig());
    }

    @Override
    public void publiserArbeidssokerRegistrert(String aktorId) {
        if (!skalArbeidssokerRegistrertPubliseres()) {
            LOG.info("Feature toggle, arbeidssokerregistrering.arbeidssokerRegistrert, er skrudd av. Det publiseres ingen Kafka-event");
            return;
        }

        Registrering registrering = Registrering.newBuilder().setAktorid(aktorId).build();
        try {
            producer.send(new ProducerRecord<>("aapen-arbeid-arbeidssoker-registrert" + getEnvSuffix(), aktorId, registrering)).get(2, TimeUnit.SECONDS);
            LOG.info("Arbeidssoker registrert-event publisert på topic, aapen-arbeid-arbeidssoker-registrert" + getEnvSuffix());

        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            LOG.warn("Sending av registrering til Kafka feilet", e);
        }
    }

    private boolean skalArbeidssokerRegistrertPubliseres() {
        return unleashService.isEnabled("arbeidssokerregistrering.arbeidssokerRegistrert");
    }

    @Override
    public void publiserHenvendelse(String aktorId) {
        if (!skalKontaktBrukerHenvendelsePubliseres()) {
            LOG.info("Feature toggle, arbeidssokerregistrering.kontantBrukerHenvendelse, er skrudd av. Det publiseres ingen Kafka-event");
            return;
        }

        Henvendelse henvendelse = Henvendelse.newBuilder().setAktorid(aktorId).build();
        try {
            henvendelseProducer.send(new ProducerRecord<>("aapen-arbeid-arbeidssoker-henvendelse-opprettet" + getEnvSuffix(), aktorId, henvendelse)).get(2, TimeUnit.SECONDS);
            LOG.info("Kontakt bruker-henvendelse publisert på topic, aapen-arbeid-arbeidssoker-henvendelse-opprettet" + getEnvSuffix());

        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            LOG.warn("Sending av henvendelse til Kafka feilet", e);
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
