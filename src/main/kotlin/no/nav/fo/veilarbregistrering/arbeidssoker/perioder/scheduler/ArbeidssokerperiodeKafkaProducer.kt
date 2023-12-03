package no.nav.fo.veilarbregistrering.arbeidssoker.perioder.scheduler

import no.nav.fo.veilarbregistrering.config.objectMapper
import no.nav.fo.veilarbregistrering.log.logger
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*

class ArbeidssokerperiodeKafkaProducer(
    private val producer: KafkaProducer<String, String>,
    private val topic: String,
) : ArbeidssokerperiodeProducer {
    override fun publiserArbeidssokerperioder(arbeidssokerperioder: ArbeidssokerperiodeHendelseMelding): Boolean {
        return try {
            val record = ProducerRecord(
                topic,
                UUID.randomUUID().toString(),
                objectMapper.writeValueAsString(arbeidssokerperioder),
            )
            producer.send(record)
            true
        } catch (e: Exception) {
            logger.error("Sending av Arbeidssokerperioder til Kafka feilet", e)
            false
        }
    }
}
