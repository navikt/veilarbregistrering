package no.nav.fo.veilarbregistrering.kafka.formidlingsgruppe

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Operation
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class FormidlingsgruppeMapper {
    internal abstract fun map(ggArenaFormidlinggruppeDto: GgArenaFormidlinggruppeDto): FormidlingsgruppeEvent

    protected fun mapFoedselsnummer(fodselsnr: String?): Foedselsnummer = fodselsnr
        ?.let { Foedselsnummer(it) }
        ?:throw IllegalArgumentException("Foedselsnummer i FormidlingsgruppeEvent er null")

    protected fun mapOperation(operation: String): Operation =
        when (operation) {
            "I" -> Operation.INSERT
            "U" -> Operation.UPDATE
            "D" -> Operation.DELETE
            else -> throw IllegalArgumentException("Ukjent op_type-verdi på Kafka: $operation")
        }

    protected fun modDato(mod_dato: String?): LocalDateTime =
        LocalDateTime.parse(mod_dato, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    internal object Factory {
        private val deleteFormidlingsgruppeMapper = DeleteFormidlingsgruppeMapper()
        private val updateFormidlingsgruppeMapper = UpdateFormidlingsgruppeMapper()
        private val insertFormidlingsgruppeMapper = InsertFormidlingsgruppeMapper()

        internal fun getInstance(ggArenaFormidlinggruppeDto: GgArenaFormidlinggruppeDto): FormidlingsgruppeMapper =
            when (ggArenaFormidlinggruppeDto.op_type) {
                "D" -> deleteFormidlingsgruppeMapper
                "U" -> updateFormidlingsgruppeMapper
                "I" -> insertFormidlingsgruppeMapper
                else -> throw IllegalArgumentException("Ukjent op_type fra Arena: " + ggArenaFormidlinggruppeDto.op_type)
            }
    }

    internal class InvalidFormidlingsgruppeEvent(s: String) : RuntimeException(s)

    companion object {
        private val json = jacksonObjectMapper().findAndRegisterModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        fun map(record: String): FormidlingsgruppeEvent {
            val ggArenaFormidlinggruppeDto: GgArenaFormidlinggruppeDto = try {
                json.readValue(record)
            } catch (e: JsonProcessingException) {
                throw IllegalArgumentException("Manglende påkrevde felter i record", e)
            }
            return Factory.getInstance(ggArenaFormidlinggruppeDto).map(ggArenaFormidlinggruppeDto)
        }
    }
}