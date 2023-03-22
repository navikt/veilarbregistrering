package no.nav.fo.veilarbregistrering.registrering.publisering.kafka

import no.nav.common.log.MDCConstants
import no.nav.fo.veilarbregistrering.log.CallId.correlationIdAsBytes
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertInternalEventV2
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertProducerV2
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.header.internals.RecordHeader
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean

internal class ArbeidssokerRegistrertKafkaProducerV2(
    private val producer: KafkaProducer<String, ArbeidssokerRegistrertInternalEventV2>,
    private val topic: String
) : ArbeidssokerRegistrertProducerV2 {

    override fun publiserArbeidssokerRegistrert(
        event: ArbeidssokerRegistrertInternalEventV2
    ): Boolean {
        return try {
            val record = ProducerRecord(
                topic,
                event.aktorId.aktorId,
                event
            )
            record.headers().add(RecordHeader(MDCConstants.MDC_CALL_ID, correlationIdAsBytes))
            val resultat = AtomicBoolean(false)
            producer.send(
                record
            ) { recordMetadata: RecordMetadata?, e: Exception? ->
                if (e != null) {
                    LOG.error(String.format("ArbeidssokerRegistrertEventV2 publisert på topic, %s", topic), e)
                    resultat.set(false)
                } else {
                    LOG.info("ArbeidssokerRegistrertEventV2 publisert: {}", recordMetadata)
                    resultat.set(true)
                }
            }.get()
            resultat.get()
        } catch (e: Exception) {
            LOG.error("Sending av arbeidssokerRegistrertEvent til Kafka feilet", e)
            false
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ArbeidssokerRegistrertKafkaProducerV2::class.java)
    }
}
