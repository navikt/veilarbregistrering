package no.nav.fo.veilarbregistrering.kafka;

import no.nav.arbeid.soker.profilering.ArbeidssokerProfilertEvent;
import no.nav.arbeid.soker.profilering.ProfilertTil;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.log.CallId;
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerProfilertProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;
import static no.nav.arbeid.soker.profilering.ProfilertTil.*;
import static no.nav.log.MDCConstants.MDC_CALL_ID;

class ArbeidssokerProfilertKafkaProducer implements ArbeidssokerProfilertProducer {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidssokerProfilertKafkaProducer.class);

    private final KafkaProducer producer;
    private final String topic;

    ArbeidssokerProfilertKafkaProducer(KafkaProducer kafkaProducer, String topic) {
        this.producer = kafkaProducer;
        this.topic = topic;
    }

    @Override
    public void publiserProfilering(AktorId aktorId, Innsatsgruppe innsatsgruppe, LocalDateTime profilertDato) {
        try {
            ArbeidssokerProfilertEvent arbeidssokerProfilertEvent = map(aktorId, innsatsgruppe, profilertDato);
            ProducerRecord<String, ArbeidssokerProfilertEvent> record = new ProducerRecord<>(topic, aktorId.asString(), arbeidssokerProfilertEvent);
            record.headers().add(new RecordHeader(MDC_CALL_ID, CallId.getCorrelationIdAsBytes()));
            producer.send(record, (recordMetadata, e) -> {
                if (e != null) {
                    LOG.error(String.format("En feil oppsto ved publisering av ArbeidssokerProfilertEvent på topic, %s", topic), e);

                } else {
                    LOG.info("ArbeidssokerProfilertEvent publisert: {}", recordMetadata);
                }
            });

        } catch (Exception e) {
            LOG.error("Sending av ArbeidssokerProfilertEvent til Kafka feilet", e);
        }
    }

    private static ArbeidssokerProfilertEvent map(AktorId aktorId, Innsatsgruppe innsatsgruppe, LocalDateTime profilertDato) {
        return ArbeidssokerProfilertEvent.newBuilder()
                .setAktorid(aktorId.asString())
                .setProfilertTil(map(innsatsgruppe))
                .setProfileringGjennomfort(ZonedDateTime.of(profilertDato, ZoneId.systemDefault()).format(ISO_ZONED_DATE_TIME))
                .build();
    }

    private static ProfilertTil map(Innsatsgruppe innsatsgruppe) {
        ProfilertTil profilering;
        switch (innsatsgruppe) {
            case STANDARD_INNSATS: {
                profilering = ANTATT_GODE_MULIGHETER;
                break;
            }
            case SITUASJONSBESTEMT_INNSATS: {
                profilering = ANTATT_BEHOV_FOR_VEILEDNING;
                break;
            }
            case BEHOV_FOR_ARBEIDSEVNEVURDERING: {
                profilering =  OPPGITT_HINDRINGER;
                break;
            }
            default: throw new EnumConstantNotPresentException(Innsatsgruppe.class, innsatsgruppe.name());
        }
        return profilering;
    }

}
