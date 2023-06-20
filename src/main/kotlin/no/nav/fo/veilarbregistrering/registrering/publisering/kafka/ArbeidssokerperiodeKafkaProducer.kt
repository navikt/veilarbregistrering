package no.nav.fo.veilarbregistrering.registrering.publisering.kafka

import no.nav.arbeid.soker.periode.ArbeidssokerperiodeEvent
import no.nav.common.log.MDCConstants
import no.nav.fo.veilarbregistrering.log.CallId.correlationIdAsBytes
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerperiodeProducer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.header.internals.RecordHeader
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean

internal class ArbeidssokerperiodeKafkaProducer(
    private val producer: KafkaProducer<String, ArbeidssokerperiodeEvent>,
    private val topic: String,
) : ArbeidssokerperiodeProducer {

    override fun publiserArbeidssokerperiode(
        arbeidssokerperiodeEvent: ArbeidssokerperiodeEvent,
    ): Boolean {
        return try {
            val record = ProducerRecord(
                topic,
                arbeidssokerperiodeEvent.getAktorId(),
                arbeidssokerperiodeEvent,
            )
            record.headers().add(RecordHeader(MDCConstants.MDC_CALL_ID, correlationIdAsBytes))
            val resultat = AtomicBoolean(false)
            producer.send(
                record,
            ) { recordMetadata: RecordMetadata?, e: Exception? ->
                if (e != null) {
                    LOG.error(String.format("ArbeidssokerperiodeEvent publisert på topic, %s", topic), e)
                    resultat.set(false)
                } else {
                    LOG.info("ArbeidssokerperiodeEvent publisert: {}", recordMetadata)
                    resultat.set(true)
                }
            }.get()
            resultat.get()
        } catch (e: Exception) {
            LOG.error("Sending av arbeidssokerperiodeEvent til Kafka feilet", e)
            false
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ArbeidssokerperiodeKafkaProducer::class.java)
    }
}
