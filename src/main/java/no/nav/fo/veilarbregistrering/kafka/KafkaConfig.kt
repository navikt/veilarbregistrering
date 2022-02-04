package no.nav.fo.veilarbregistrering.kafka

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import no.nav.arbeid.soker.profilering.ArbeidssokerProfilertEvent
import no.nav.arbeid.soker.registrering.ArbeidssokerRegistrertEvent
import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerService
import no.nav.fo.veilarbregistrering.config.isProduction
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerProfilertProducer
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertProducer
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.security.auth.SecurityProtocol
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.util.*

@Configuration
class KafkaConfig {

    @Bean
    fun arbeidssokerRegistrertKafkaProducerAiven(kafkaProducerAiven: KafkaProducer<String, ArbeidssokerRegistrertEvent>): ArbeidssokerRegistrertProducer {
        return ArbeidssokerRegistrertKafkaProducer(
            kafkaProducerAiven,
            "paw.arbeidssoker-registrert-v1"
        )
    }

    @Bean
    fun arbeidssokerProfilertKafkaProducerAiven(kafkaProducerAiven: KafkaProducer<String, ArbeidssokerProfilertEvent>): ArbeidssokerProfilertProducer {
        return ArbeidssokerProfilertKafkaProducer(
            kafkaProducerAiven,
            "paw.arbeidssoker-profilert-v1"
        )
    }

    @Bean
    fun kafkaProducerAiven(): KafkaProducer<*, *> {
        return KafkaProducer<Any?, Any?>(kafkaPropertiesAiven())
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
        unleashClient: UnleashClient,
        arbeidssokerService: ArbeidssokerService
    ): FormidlingsgruppeKafkaConsumer {
        return FormidlingsgruppeKafkaConsumer(
            formidlingsgruppeKafkaConsumerProperties(),
            "gg-arena-formidlinggruppe-v1-$envSuffix",
            arbeidssokerService, unleashClient
        )
    }

    @Bean
    fun formidlingsgruppeKafkaConsumerProperties(): Properties {
        val properties = Properties()
        properties[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = System.getenv("KAFKA_SERVERS")
        properties[ConsumerConfig.GROUP_ID_CONFIG] = groupIdForFormidlingsgruppeConsumer
        properties[KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = System.getenv("KAFKA_SCHEMA")
        properties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] =
            StringDeserializer::class.java
        properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] =
            StringDeserializer::class.java
        properties[KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG] = true
        properties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = autoOffsetResetStrategy
        properties[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false
        if (System.getProperty("SERVICEUSER_PASSWORD") != null) {
            properties.putAll(securityConfig)
        }
        return properties
    }

    companion object {
        // «earliest» gir oss «at least once»-prosessering av meldinger. Med idempotency-håndtering av meldingene,
        // vil dette gi oss «eventual consistency».
        private const val autoOffsetResetStrategy: String = "earliest"
        private const val groupIdForFormidlingsgruppeConsumer: String = "veilarbregistrering-FormidlingsgruppeKafkaConsumer-02"

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

        private val securityConfig: Properties = Properties().apply {
            this[SaslConfigs.SASL_MECHANISM] = "PLAIN"
            this[CommonClientConfigs.SECURITY_PROTOCOL_CONFIG] = "SASL_PLAINTEXT"
            this[SaslConfigs.SASL_JAAS_CONFIG] =
                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" +
                        System.getProperty("SERVICEUSER_USERNAME") + "\" password=\"" +
                        System.getProperty("SERVICEUSER_PASSWORD") + "\";"
            if (System.getenv("NAV_TRUSTSTORE_PATH") != null) {
                this[CommonClientConfigs.SECURITY_PROTOCOL_CONFIG] = "SASL_SSL"
                this[SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG] =
                    File(System.getenv("NAV_TRUSTSTORE_PATH")).absolutePath
                this[SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG] = System.getenv("NAV_TRUSTSTORE_PASSWORD")
            }
        }

        private val envSuffix: String = if (isProduction()) "p" else "q"
    }
}