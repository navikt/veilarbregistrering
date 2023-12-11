package no.nav.fo.veilarbregistrering.kafka

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import io.getunleash.Unleash
import no.nav.arbeid.soker.profilering.ArbeidssokerProfilertEvent
import no.nav.arbeid.soker.registrering.ArbeidssokerRegistrertEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeMottakService
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.kafka.FormidlingsgruppeKafkaConsumer
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortMottakService
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.kafka.MeldekortKafkaConsumer
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.scheduler.ArbeidssokerperiodeKafkaProducer
import no.nav.fo.veilarbregistrering.arbeidssoker.perioder.scheduler.ArbeidssokerperiodeProducer
import no.nav.fo.veilarbregistrering.config.isProduction
import no.nav.fo.veilarbregistrering.registrering.ordinaer.scheduler.OpplysningerMottattKafkaProducer
import no.nav.fo.veilarbregistrering.registrering.ordinaer.scheduler.OpplysningerMottattProducer
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerProfilertProducer
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertProducer
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertProducerV2
import no.nav.fo.veilarbregistrering.registrering.publisering.kafka.ArbeidssokerProfilertKafkaProducer
import no.nav.fo.veilarbregistrering.registrering.publisering.kafka.ArbeidssokerRegistrertKafkaProducer
import no.nav.fo.veilarbregistrering.registrering.publisering.kafka.ArbeidssokerRegistrertKafkaProducerV2
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.security.auth.SecurityProtocol
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class KafkaConfig {

    @Bean
    fun arbeidssokerRegistrertKafkaProducerAiven(kafkaProducerAiven: KafkaProducer<String, ArbeidssokerRegistrertEvent>): ArbeidssokerRegistrertProducer {
        return ArbeidssokerRegistrertKafkaProducer(kafkaProducerAiven, "paw.arbeidssoker-registrert-v1")
    }

    @Bean
    fun arbeidssokerRegistrertKafkaProducerV2Aiven(kafkaProducerStringSerializerAiven: KafkaProducer<String, String>): ArbeidssokerRegistrertProducerV2 {
        return ArbeidssokerRegistrertKafkaProducerV2(kafkaProducerStringSerializerAiven, "paw.arbeidssoker-registrert-v2")
    }

    @Bean
    fun arbeidssokerProfilertKafkaProducerAiven(kafkaProducerAiven: KafkaProducer<String, ArbeidssokerProfilertEvent>): ArbeidssokerProfilertProducer {
        return ArbeidssokerProfilertKafkaProducer(kafkaProducerAiven, "paw.arbeidssoker-profilert-v1")
    }

    @Bean
    fun arbeidssokerperiodeKafkaProducerAiven(kafkaProducerStringSerializerAiven: KafkaProducer<String, String>): ArbeidssokerperiodeProducer {
        return ArbeidssokerperiodeKafkaProducer(kafkaProducerStringSerializerAiven, "paw.arbeidssokerperioder-overforing-beta-v1")
    }

    @Bean
    fun opplysningerOmArbeidssokerProducerAvien(kafkaProducerStringSerializerAiven: KafkaProducer<String, String>): OpplysningerMottattProducer =
        OpplysningerMottattKafkaProducer(kafkaProducerStringSerializerAiven, "paw.veilarb-opplysninger-mottatt-v1")

    @Bean
    fun kafkaProducerAiven(): KafkaProducer<*, *> {
        return KafkaProducer<Any?, Any?>(kafkaPropertiesAiven())
    }

    @Bean
    fun kafkaProducerStringSerializerAiven(): KafkaProducer<String, String> {
        return KafkaProducer<String, String>(
            kafkaPropertiesAiven().apply {
                this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
            }
        )
    }

    @Bean
    fun kafkaPropertiesAiven(): Properties {
        val properties = Properties()
        properties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = System.getenv("KAFKA_BROKERS")
        properties[KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = System.getenv("KAFKA_SCHEMA_REGISTRY")
        val basicAuth =
            System.getenv("KAFKA_SCHEMA_REGISTRY_USER") + ":" + System.getenv("KAFKA_SCHEMA_REGISTRY_PASSWORD")
        properties[SchemaRegistryClientConfig.BASIC_AUTH_CREDENTIALS_SOURCE] = "USER_INFO" // magic constant, yay!
        properties[SchemaRegistryClientConfig.USER_INFO_CONFIG] = basicAuth
        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaAvroSerializer::class.java
        properties[ProducerConfig.CLIENT_ID_CONFIG] = "paw-veilarbregistrering"
        properties[ProducerConfig.ACKS_CONFIG] = "all"
        properties[ProducerConfig.RETRIES_CONFIG] = 0
        properties.putAll(aivenSecurityConfig)
        return properties
    }

    @Bean
    fun formidlingsgruppeKafkaConsumer(
        unleashClient: Unleash,
        formidlingsgruppeMottakService: FormidlingsgruppeMottakService
    ): FormidlingsgruppeKafkaConsumer {
        val envSuffix = if (isProduction()) "p" else "q"
        return FormidlingsgruppeKafkaConsumer(
            formidlingsgruppeKafkaConsumerProperties(),
            "teamarenanais.aapen-arena-formidlingsgruppeendret-v1-$envSuffix",
            formidlingsgruppeMottakService, unleashClient
        )
    }

    private fun formidlingsgruppeKafkaConsumerProperties(): Properties {
        val groupIdForFormidlingsgruppeConsumer = "veilarbregistrering-FormidlingsgruppeKafkaConsumer-03"
        return kafkaConsumerProperties(groupIdForFormidlingsgruppeConsumer)
    }

    @Bean
    fun meldekortKafkaConsumer(
        unleashClient: Unleash,
        meldekortMottakService: MeldekortMottakService
    ): MeldekortKafkaConsumer {
        val envSuffix = if (isProduction()) "p" else "q1"
        return MeldekortKafkaConsumer(
            meldekortKafkaConsumerProperties(),
            "meldekort.aapen-meldeplikt-meldekortgodkjentalle-v1-$envSuffix",
            unleashClient,
            meldekortMottakService
        )
    }

    private fun meldekortKafkaConsumerProperties(): Properties {
        val groupIdForMeldekortConsumer = "veilarbregistrering-MeldekortKafkaConsumer-01"
        return kafkaConsumerProperties(groupIdForMeldekortConsumer)
    }

    private fun kafkaConsumerProperties(groupId: String): Properties {
        val properties = Properties()
        properties[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = System.getenv("KAFKA_BROKERS")
        properties[ConsumerConfig.GROUP_ID_CONFIG] = groupId
        properties[KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = System.getenv("KAFKA_SCHEMA_REGISTRY")
        properties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        properties[KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG] = true
        properties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = autoOffsetResetStrategy
        properties[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false
        properties.putAll(aivenSecurityConfig)
        return properties
    }

    companion object {
        // «earliest» gir oss «at least once»-prosessering av meldinger. Med idempotency-håndtering av meldingene,
        // vil dette gi oss «eventual consistency».
        private const val autoOffsetResetStrategy: String = "earliest"
        private val aivenSecurityConfig: Properties = Properties().apply {
            val credstorePassword = System.getenv("KAFKA_CREDSTORE_PASSWORD")
            this[CommonClientConfigs.SECURITY_PROTOCOL_CONFIG] = SecurityProtocol.SSL.name
            this[SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG] = "jks"
            this[SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG] = System.getenv("KAFKA_TRUSTSTORE_PATH")
            this[SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG] = credstorePassword
            this[SslConfigs.SSL_KEYSTORE_TYPE_CONFIG] = "PKCS12"
            this[SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG] = System.getenv("KAFKA_KEYSTORE_PATH")
            this[SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG] = credstorePassword
        }
    }
}
