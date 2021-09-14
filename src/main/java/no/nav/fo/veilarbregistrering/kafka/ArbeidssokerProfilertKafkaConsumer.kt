package no.nav.fo.veilarbregistrering.kafka

import no.nav.arbeid.soker.profilering.ArbeidssokerProfilertEvent
import no.nav.common.log.MDCConstants
import no.nav.fo.veilarbregistrering.log.CallId
import no.nav.fo.veilarbregistrering.log.loggerFor
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.header.internals.RecordHeader
import org.slf4j.MDC
import java.time.Duration
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer

/**
 * 1. Konsumerer våre egen "Arbeidssøker profilert"
 * 2. Kjører i evig løkka
 * 3. Produserer hver melding på nytt med producer konfigurert for Aiven
 */
class ArbeidssokerProfilertKafkaConsumer internal constructor(
    private val kafkaConsumerProperties: Properties,
    private val kafkaProducerProperties: Properties,
    private val konsumentTopic: String,
    private val produsentTopic: String,
) : Runnable {

    init {
        Executors.newSingleThreadScheduledExecutor()
            .scheduleWithFixedDelay(
                this,
                180000,
                500,
                TimeUnit.MILLISECONDS
            )
    }

    override fun run() {
        val mdcTopicKey = "topic"
        val mdcOffsetKey = "offset"
        val mdcPartitionKey = "partition"
        MDC.put(mdcTopicKey, konsumentTopic)
        LOG.info("Running")
        try {

            KafkaConsumer<String, ArbeidssokerProfilertEvent>(kafkaConsumerProperties).use { consumer ->
                consumer.subscribe(listOf(konsumentTopic))
                LOG.info("Subscribing to {}", konsumentTopic)
                while (true) {
                    val consumerRecords = consumer.poll(Duration.ofMinutes(2))
                    consumerRecords.forEach(Consumer { record: ConsumerRecord<String?, ArbeidssokerProfilertEvent?> ->
                        CallId.leggTilCallId()
                        MDC.put(mdcOffsetKey, record.offset().toString())
                        MDC.put(mdcPartitionKey, record.partition().toString())
                        try {
                            record.value()?.let { republiserMelding(it) }
                                ?: LOG.error("Fant melding for {} uten verdi/body", record.key())
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

    private fun republiserMelding(event: ArbeidssokerProfilertEvent) {
        LOG.info(
            "Republiserer profilert-melding til Aiven for {} {}",
            event.getAktorid(),
            event.getProfileringGjennomfort()
        )

        KafkaProducer<String, ArbeidssokerProfilertEvent>(kafkaProducerProperties).use {

            val record = ProducerRecord(
                produsentTopic,
                event.getAktorid(),
                event
            )
            record.headers().add(RecordHeader(MDCConstants.MDC_CALL_ID, CallId.getCorrelationIdAsBytes()))
            val resultat = AtomicBoolean(false)
            it.send(record) { recordMetadata: RecordMetadata?, e: Exception? ->
                if (e != null) {
                    LOG.error(
                        String.format(
                            "ArbeidssokerProfilertEvent kunne ikke publiseres på topic, %s",
                            produsentTopic
                        ), e
                    )
                    resultat.set(false)
                } else {
                    LOG.info(
                        "ArbeidssokerProfilertEvent publisert: {}",
                        recordMetadata
                    )
                    resultat.set(true)
                }
            }.get()
            if (!resultat.get()) throw RuntimeException("En feil oppsto under publisering av melding på topic $produsentTopic")
        }

    }

    companion object {
        private val LOG = loggerFor<ArbeidssokerProfilertKafkaConsumer>()
    }
}