package no.nav.fo.veilarbregistrering.kafka.meldekort

import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.log.MDCConstants
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortMottakService
import no.nav.fo.veilarbregistrering.config.objectMapper
import no.nav.fo.veilarbregistrering.log.CallId
import no.nav.fo.veilarbregistrering.log.logger
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.MDC
import java.time.Duration
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MeldekortKafkaConsumer internal constructor(
    private val kafkaConsumerProperties: Properties,
    private val topic: String,
    private val unleashClient: UnleashClient,
    private val meldekortMottakService: MeldekortMottakService
) : Runnable {
    init {
        val forsinkelseIMinutterVedOppstart = 5
        val forsinkelseIMinutterVedStopp = 5
        Executors.newSingleThreadScheduledExecutor()
            .scheduleWithFixedDelay(
                this,
                forsinkelseIMinutterVedOppstart.toLong(),
                forsinkelseIMinutterVedStopp.toLong(),
                TimeUnit.MINUTES
            )
    }

    override fun run() {
        if (stopKonsumeringAvMeldekort()) {
            logger.info("Kill-switch '$KILL_SWITCH_TOGGLE_NAME' aktivert. Hopper over lesing fra kafka")
            return
        }
        MDC.put(mdcTopicKey, topic)
        logger.info("Running")

        try {
            KafkaConsumer<String, String>(kafkaConsumerProperties).use { consumer ->
                consumer.subscribe(listOf(topic))
                logger.info("Subscribing to {}", topic)
                while (!stopKonsumeringAvMeldekort()) {
                    val consumerRecords = consumer.poll(Duration.ofMinutes(2))
                    logger.info("Leser {} events fra topic {}", consumerRecords.count(), topic)

                    if (consumerRecords.isEmpty) {
                        logger.info("Ingen nye events - venter på neste poll ...")
                        continue
                    }

                    consumerRecords.forEach { record: ConsumerRecord<String, String> ->
                        CallId.leggTilCallId()
                        try {
                            behandleMeldekortEvent(record)
                        } catch (e: IllegalArgumentException) {
                            logger.warn("Behandling av record feilet: ${record.value()}", e)
                        } catch (e: RuntimeException) {
                            logger.error("Behandling av record feilet: ${record.value()}", e)
                            throw e
                        } finally {
                            MDC.remove(MDCConstants.MDC_CALL_ID)
                        }
                    }
                    logger.info("Nyeste offset er: ${consumerRecords.last().offset()}")
                    consumer.commitSync()
                }
                logger.info("Stopper lesing av topic etter at toggle `{}` er skrudd på",
                    KILL_SWITCH_TOGGLE_NAME
                )
            }
        } catch (e: Exception) {
            logger.error("Det oppstod en ukjent feil ifm. konsumering av events fra $topic", e)
        } finally {
            MDC.remove(MDCConstants.MDC_CALL_ID)
            MDC.remove(mdcTopicKey)
        }
    }

    private fun behandleMeldekortEvent(event: ConsumerRecord<String, String>) {
        val meldekortEventDto = objectMapper.readValue(event.value(), MeldekortEventDto::class.java)
        meldekortMottakService.behandleMeldekortEvent(meldekortEventDto.map())
    }

    private fun stopKonsumeringAvMeldekort() = unleashClient.isEnabled(KILL_SWITCH_TOGGLE_NAME)

    companion object {
        private const val mdcTopicKey = "topic"
        private const val KILL_SWITCH_TOGGLE_NAME = "veilarbregistrering.stopKonsumeringAvMeldekort"
    }

}