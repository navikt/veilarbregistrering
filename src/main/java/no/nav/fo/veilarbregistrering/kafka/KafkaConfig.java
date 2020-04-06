package no.nav.fo.veilarbregistrering.kafka;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import no.nav.fo.veilarbregistrering.registrering.bruker.DatakvalitetOppholdstillatelseService;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Collections;
import java.util.Properties;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;

@Configuration
public class KafkaConfig {

    @Bean
    ArbeidssokerRegistrertKafkaProducer arbeidssokerRegistrertKafkaProducer(
            KafkaProducer kafkaProducer, UnleashService unleashService) {
        return new ArbeidssokerRegistrertKafkaProducer(
                kafkaProducer,
                unleashService,
                "aapen-arbeid-arbeidssoker-registrert" + getEnvSuffix());
    }

    @Bean
    KontaktBrukerOpprettetKafkaProducer kontaktBrukerOpprettetKafkaProducer(
            KafkaProducer kafkaProducer, UnleashService unleashService) {
        return new KontaktBrukerOpprettetKafkaProducer(
                kafkaProducer,
                unleashService,
                "aapen-arbeid-arbeidssoker-kontaktbruker-opprettet" + getEnvSuffix());
    }

    @Bean
    KafkaProducer kafkaProducer() {
        return new KafkaProducer(kafkaProperties());
    }

    @Bean
    Properties kafkaProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getenv("KAFKA_SERVERS"));
        properties.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, getenv("KAFKA_SCHEMA"));
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "paw-veilarbregistrering");
        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        if (System.getProperty("SRVVEILARBREGISTRERING_PASSWORD") != null) {
            properties.putAll(getSecurityConfig());
        }
        return properties;
    }

    @Bean
    KontaktBrukerOpprettetKafkaConsumer kontaktBrukerOpprettetKafkaConsumer(
            KafkaConsumer kafkaConsumer,
            UnleashService unleashService,
            DatakvalitetOppholdstillatelseService datakvalitetOppholdstillatelseService
    ) {
        return new KontaktBrukerOpprettetKafkaConsumer(
                kafkaConsumer,
                unleashService,
                datakvalitetOppholdstillatelseService);
    }

    @Bean
    KafkaConsumer kafkaConsumer() {
        KafkaConsumer kafkaConsumer = new KafkaConsumer(kafkaConsumerProperties());
        kafkaConsumer.subscribe(Collections.singletonList("aapen-arbeid-arbeidssoker-kontaktbruker-opprettet" + getEnvSuffix()));
        return kafkaConsumer;
    }

    @Bean
    Properties kafkaConsumerProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getenv("KAFKA_SERVERS"));
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, getGroupId());
        properties.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, getenv("KAFKA_SCHEMA"));
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        properties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return properties;
    }

    private String getGroupId() {
        return "veilarbregistrering";
    }

    private static Properties getSecurityConfig() {
        Properties props = new Properties();
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(
                SaslConfigs.SASL_JAAS_CONFIG,
                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" +
                        getProperty("SRVVEILARBREGISTRERING_USERNAME") + "\" password=\"" +
                        getProperty("SRVVEILARBREGISTRERING_PASSWORD") + "\";"
        );
        if (getenv("NAV_TRUSTSTORE_PATH") != null) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, new File(getenv("NAV_TRUSTSTORE_PATH")).getAbsolutePath());
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, getenv("NAV_TRUSTSTORE_PASSWORD"));
        }
        return props;
    }

    private static String getEnvSuffix() {
        String envName = getenv("APP_ENVIRONMENT_NAME");
        if (envName != null) {
            return "-" + envName.toLowerCase();
        } else {
            return "";
        }
    }
}
