package no.nav.fo.veilarbregistrering.kafka

import no.nav.arbeid.soker.registrering.ArbeidssokerRegistrertEvent
import no.nav.common.log.MDCConstants
import no.nav.fo.veilarbregistrering.log.CallId
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.header.internals.RecordHeader
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.time.Duration
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

/**
 * 1. Konsumerer våre egen "Arbeidssøker registrert"
 * 2. Kjører i evig løkka
 * 3. Produserer hver melding på nytt med producer konfigurert for Aiven
 */
class ArbeidssokerRegistrertKafkaConsumer  internal constructor(
    private val kafkaConsumerProperties: Properties,
    private val kafkaProducerProperties: Properties,
    private val konsumentTopic: String,
    private val produsentTopic: String,
) : Runnable {

    init {
        Executors.newSingleThreadScheduledExecutor()
            .scheduleWithFixedDelay(
                this,
                5,
                5,
                TimeUnit.MINUTES
            )
    }

    override fun run() {
        val mdcTopicKey = "topic"
        val mdcOffsetKey = "offset"
        val mdcPartitionKey = "partition"
        MDC.put(mdcTopicKey, konsumentTopic)
        LOG.info("Running")
        try {

            KafkaConsumer<String, ArbeidssokerRegistrertEvent>(kafkaConsumerProperties).use { consumer ->
                consumer.subscribe(listOf(konsumentTopic))
                LOG.info("Subscribing to {}", konsumentTopic)
                while (true) {
                    val consumerRecords = consumer.poll(Duration.ofMinutes(2))
                    LOG.info("Leser {} events fra topic {}", consumerRecords.count(), konsumentTopic)
                    consumerRecords.forEach(Consumer { record: ConsumerRecord<String?, ArbeidssokerRegistrertEvent?> ->
                        CallId.leggTilCallId()
                        MDC.put(mdcOffsetKey, record.offset().toString())
                        MDC.put(mdcPartitionKey, record.partition().toString())
                        try {
                            record.value()?.let { republiserMelding(it) } ?: LOG.error("Fant melding for {} uten verdi/body", record.key())
                        } catch (e: RuntimeException) {
                            LOG.error(String.format("Behandling av record feilet: %s", record.value()), e)
                            throw e
                        } finally {
                            MDC.remove(MDCConstants.MDC_CALL_ID)
                            MDC.remove(mdcOffsetKey)
                            MDC.remove(mdcPartitionKey)
                        }
                    })
                    consumer.commitSync()
                }
            }
        } catch (e: Exception) {
            LOG.error(String.format("Det oppstod en ukjent feil ifm. konsumering av events fra %s", konsumentTopic), e)
        } finally {
            MDC.remove(MDCConstants.MDC_CALL_ID)
            MDC.remove(mdcTopicKey)
        }
    }

    private fun republiserMelding(event: ArbeidssokerRegistrertEvent) {
        LOG.info("Republiserer registrert melding til Aiven for {} {}", event.getAktorid(), event.getRegistreringOpprettet())

        try {
            KafkaProducer<String, ArbeidssokerRegistrertEvent>(kafkaProducerProperties).use {

                val record = ProducerRecord(
                    produsentTopic,
                    event.getAktorid(),
                    event
                )
                record.headers().add(RecordHeader(MDCConstants.MDC_CALL_ID, CallId.getCorrelationIdAsBytes()))

                it.send(record) { recordMetadata: RecordMetadata?, e: Exception? ->
                    if (e != null) {
                        LOG.error(
                            String.format(
                                "ArbeidssokerRegistrertEvent publisert på topic, %s",
                                produsentTopic
                            ), e
                        )
                    } else {
                        LOG.info(
                            "ArbeidssokerRegistrertEvent publisert: {}",
                            recordMetadata
                        )
                    }
                }
            }
        } catch (e: Exception) {
            LOG.error("Sending av arbeidssokerRegistrertEvent til Kafka aiven feilet", e)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ArbeidssokerRegistrertKafkaConsumer::class.java)
    }
}