package no.nav.fo.veilarbregistrering.kafka

import no.nav.arbeid.soker.registrering.ArbeidssokerRegistrertEvent
import no.nav.common.log.MDCConstants
import no.nav.fo.veilarbregistrering.kafka.ArbeidssokerRegistrertMapper.map
import no.nav.fo.veilarbregistrering.log.CallId.correlationIdAsBytes
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertInternalEvent
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertProducer
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.header.internals.RecordHeader
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean

internal class ArbeidssokerRegistrertKafkaProducer(
    private val producer: KafkaProducer<String, ArbeidssokerRegistrertEvent>,
    private val topic: String
) : ArbeidssokerRegistrertProducer {
    override fun publiserArbeidssokerRegistrert(
        event: ArbeidssokerRegistrertInternalEvent?
    ): Boolean {
        return try {
            val arbeidssokerRegistrertEvent = map(event!!)
            val record = ProducerRecord(
                topic, event.aktorId.aktorId, arbeidssokerRegistrertEvent
            )
            record.headers().add(RecordHeader(MDCConstants.MDC_CALL_ID, correlationIdAsBytes))
            val resultat = AtomicBoolean(false)
            producer.send(record, Callback { recordMetadata: RecordMetadata?, e: Exception? ->
                if (e != null) {
                    LOG.error(String.format("ArbeidssokerRegistrertEvent publisert på topic, %s", topic), e)
                    resultat.set(false)
                } else {
                    LOG.info("ArbeidssokerRegistrertEvent publisert: {}", recordMetadata)
                    resultat.set(true)
                }
            }).get()
            resultat.get()
        } catch (e: Exception) {
            LOG.error("Sending av arbeidssokerRegistrertEvent til Kafka feilet", e)
            false
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ArbeidssokerRegistrertKafkaProducer::class.java)
    }
}