package no.nav.fo.veilarbregistrering.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import javax.ws.rs.ext.ParamConverter
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer
import kotlin.Throws
import java.io.IOException
import java.sql.Date
import java.sql.Timestamp
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern

object DateConfiguration {
    private val YEAR_START_1800 = ZonedDateTime.of(1800, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault())
    private val DEFAULT_ZONE: ZoneId = ZoneId.of("Europe/Paris")
    private var module: SimpleModule? = null

    fun dateModule(): Module {
        return module ?: SimpleModule().also {
            val localDateProvider = LocalDateProvider()
            val localDateTimeProvider = LocalDateTimeProvider()
            val zonedDateTimeProvider = ZonedDateTimeProvider()
            val dateProvider = DateProvider()
            val timestampProvider = TimestampProvider()
            val sqlDateProvider = SqlDateProvider()
            it.addSerializer(localDateProvider.serializer)
            it.addDeserializer(localDateProvider.targetClass, localDateProvider.deSerializer)
            it.addSerializer(localDateTimeProvider.serializer)
            it.addDeserializer(localDateTimeProvider.targetClass, localDateTimeProvider.deSerializer)
            it.addSerializer(zonedDateTimeProvider.serializer)
            it.addDeserializer(zonedDateTimeProvider.targetClass, zonedDateTimeProvider.deSerializer)
            it.addSerializer(dateProvider.serializer)
            it.addDeserializer(dateProvider.targetClass, dateProvider.deSerializer)
            it.addSerializer(timestampProvider.serializer)
            it.addDeserializer(timestampProvider.targetClass, timestampProvider.deSerializer)
            it.addSerializer(sqlDateProvider.serializer)
            it.addDeserializer(sqlDateProvider.targetClass, sqlDateProvider.deSerializer)
            module = it
        }
    }

    private fun notNullOrEmpty(maybeString: String?): String? {
        return maybeString?.trim { it <= ' ' }?.ifEmpty { null }
    }

    private abstract class BaseProvider<T>(val targetClass: Class<T>) : ParamConverter<T> {
        val serializer: JsonSerializer<T>
        val deSerializer: JsonDeserializer<T>
        protected abstract fun toValue(zonedDateTime: ZonedDateTime): T
        protected abstract fun from(value: T): ZonedDateTime
        override fun fromString(value: String?): T? {
            return value?.let {
                toValue(ZonedDateTime.parse(it))
            }
        }

        override fun toString(value: T): String {
            val zonedDateTime = from(value)
            // eldgamle datoer med sekund-offset skaper problemer for bl.a. moment js.
            // velger derfor å formattere gamle datoer uten offset
            return if (zonedDateTime.isBefore(YEAR_START_1800)) Instant.from(zonedDateTime).toString() else zonedDateTime.format(
                DateTimeFormatter.ISO_OFFSET_DATE_TIME
            )
        }

        init {
            serializer = object : StdScalarSerializer<T>(targetClass) {
                @Throws(IOException::class)
                override fun serialize(value: T, jgen: JsonGenerator, provider: SerializerProvider) {
                    jgen.writeString(this@BaseProvider.toString(value))
                }
            }
            deSerializer = object : StdScalarDeserializer<T>(targetClass) {
                @Throws(IOException::class)
                override fun deserialize(p: JsonParser, ctxt: DeserializationContext): T? {
                    return p.text?.let {
                        fromString(it) }
                }
            }
        }
    }

    private class LocalDateProvider : BaseProvider<LocalDate>(
        LocalDate::class.java
    ) {
        override fun toValue(zonedDateTime: ZonedDateTime): LocalDate {
            return zonedDateTime.toLocalDate()
        }

        override fun from(value: LocalDate): ZonedDateTime {
            return value.atTime(LocalTime.NOON).atZone(DEFAULT_ZONE)
        }

        override fun fromString(value: String?): LocalDate? {
            return notNullOrEmpty(value)
                ?.takeIf { isLocalDate(it) }
                ?.let { LocalDate.parse(it) }
                ?: super.fromString(value)
        }

        private fun isLocalDate(dateString: String): Boolean {
            return YYYY_MM_DD_PATTERN.matcher(dateString.trim { it <= ' ' }).matches()
        }

        companion object {
            private val YYYY_MM_DD_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$")
        }
    }

    private class LocalDateTimeProvider : BaseProvider<LocalDateTime>(
        LocalDateTime::class.java
    ) {
        override fun toValue(zonedDateTime: ZonedDateTime): LocalDateTime {
            return zonedDateTime.withZoneSameInstant(DEFAULT_ZONE).toLocalDateTime()
        }

        override fun from(value: LocalDateTime): ZonedDateTime {
            return value.atZone(DEFAULT_ZONE)
        }

        override fun fromString(value: String?): LocalDateTime? {
            return notNullOrEmpty(value)
                ?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
                ?: super.fromString(value)
        }
    }

    private class ZonedDateTimeProvider : BaseProvider<ZonedDateTime>(
        ZonedDateTime::class.java
    ) {
        override fun toValue(zonedDateTime: ZonedDateTime): ZonedDateTime {
            return zonedDateTime
        }

        override fun from(value: ZonedDateTime): ZonedDateTime {
            return value
        }
    }

    private class DateProvider : BaseProvider<java.util.Date>(java.util.Date::class.java) {
        override fun toValue(zonedDateTime: ZonedDateTime): java.util.Date {
            return java.util.Date.from(zonedDateTime.toInstant())
        }

        public override fun from(value: java.util.Date): ZonedDateTime {
            return ZonedDateTime.ofInstant(Instant.ofEpochMilli(value.time), DEFAULT_ZONE)
        }
    }

    private class SqlDateProvider : BaseProvider<Date>(Date::class.java) {
        private val dateProvider = DateProvider()
        override fun toValue(zonedDateTime: ZonedDateTime): Date {
            return Date.valueOf(zonedDateTime.toLocalDate())
        }

        override fun from(value: Date): ZonedDateTime {
            return dateProvider.from(value)
        }
    }

    private class TimestampProvider : BaseProvider<Timestamp>(Timestamp::class.java) {
        private val dateProvider = DateProvider()
        override fun toValue(zonedDateTime: ZonedDateTime): Timestamp {
            return Timestamp.from(zonedDateTime.toInstant())
        }

        override fun from(value: Timestamp): ZonedDateTime {
            return dateProvider.from(value)
        }
    }
}