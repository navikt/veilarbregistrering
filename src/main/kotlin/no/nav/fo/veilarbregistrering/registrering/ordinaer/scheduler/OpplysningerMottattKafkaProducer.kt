package no.nav.fo.veilarbregistrering.registrering.ordinaer.scheduler

import no.nav.fo.veilarbregistrering.config.objectMapper
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.paw.arbeidssokerregisteret.intern.v1.OpplysningerOmArbeidssoekerMottatt
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.math.BigInteger
import java.util.*

class OpplysningerMottattKafkaProducer(
    private val producer: KafkaProducer<String, String>,
    private val topic: String,
) : OpplysningerMottattProducer {
    override fun publiserOpplysningerMottatt(opplysningerOmArbeidssoekerMottatt: OpplysningerOmArbeidssoekerMottatt): Boolean =
        try {
            val record = ProducerRecord(
                topic,
                UUID.randomUUID().toString(),
                objectMapper.writeValueAsString(opplysningerOmArbeidssoekerMottatt),
            )
            producer.send(record)
            true
        } catch (e: Exception) {
            logger.error("Sending av 'opplysninger mottatt' til Kafka feilet", e)
            false
        }
    }
