package no.nav.fo.veilarbregistrering.registrering.publisering.kafka

import no.nav.arbeid.soker.profilering.ArbeidssokerProfilertEvent
import no.nav.arbeid.soker.profilering.ProfilertTil
import no.nav.common.log.MDCConstants
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.log.CallId.correlationIdAsBytes
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerProfilertProducer
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.header.internals.RecordHeader
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

internal class ArbeidssokerProfilertKafkaProducer(
    private val producer: KafkaProducer<String, ArbeidssokerProfilertEvent>,
    private val topic: String
) : ArbeidssokerProfilertProducer {
    override fun publiserProfilering(aktorId: AktorId, innsatsgruppe: Innsatsgruppe, profilertDato: LocalDateTime) {
        try {
            val arbeidssokerProfilertEvent = map(aktorId, innsatsgruppe, profilertDato)
            val record = ProducerRecord(
                topic, aktorId.aktorId, arbeidssokerProfilertEvent
            )
            record.headers().add(RecordHeader(MDCConstants.MDC_CALL_ID, correlationIdAsBytes))

            producer.send(record, Callback { recordMetadata: RecordMetadata, e: Exception? ->
                when(e) {
                    null -> LOG.info("ArbeidssokerProfilertEvent publisert: {}", recordMetadata)
                    else -> LOG.error(String.format("En feil oppsto ved publisering av ArbeidssokerProfilertEvent på topic, %s", topic), e)
                }
            })

        } catch (e: Exception) {
            LOG.error("Sending av ArbeidssokerProfilertEvent til Kafka feilet", e)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ArbeidssokerProfilertKafkaProducer::class.java)
        private fun map(
            aktorId: AktorId,
            innsatsgruppe: Innsatsgruppe,
            profilertDato: LocalDateTime
        ): ArbeidssokerProfilertEvent {
            return ArbeidssokerProfilertEvent.newBuilder()
                .setAktorid(aktorId.aktorId)
                .setProfilertTil(map(innsatsgruppe))
                .setProfileringGjennomfort(
                    ZonedDateTime.of(profilertDato, ZoneId.systemDefault())
                        .format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
                )
                .build()
        }

        private fun map(innsatsgruppe: Innsatsgruppe): ProfilertTil =
            when (innsatsgruppe) {
                Innsatsgruppe.STANDARD_INNSATS -> {
                    ProfilertTil.ANTATT_GODE_MULIGHETER
                }
                Innsatsgruppe.SITUASJONSBESTEMT_INNSATS -> {
                    ProfilertTil.ANTATT_BEHOV_FOR_VEILEDNING
                }
                Innsatsgruppe.BEHOV_FOR_ARBEIDSEVNEVURDERING -> {
                    ProfilertTil.OPPGITT_HINDRINGER
                }
            }
    }
}