package no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.kafka

import no.nav.fo.veilarbregistrering.FileToJson.toJson
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Operation
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.kafka.FormidlingsgruppeMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class FormidlingsgruppeMapperTest {

    @Test
    fun `Kaster exception ved manglende mod dato`() {
        val json = toJson("/kafka/formidlingsgruppe_uten_mod_dato.json")
        assertThrows<IllegalArgumentException> { FormidlingsgruppeMapper.map(json) }
    }

    @Test
    fun `Kaster exception ved manglende op_type`() {
        val json = toJson("/kafka/formidlingsgruppe_uten_op_type_I.json")
        assertThrows<IllegalArgumentException> { FormidlingsgruppeMapper.map(json) }
    }

    @Test
    fun `Kaster exception ved manglende person_id_status`() {
        val json = toJson("/kafka/formidlingsgruppe_uten_person_id_status.json")
        assertThrows<IllegalArgumentException> { FormidlingsgruppeMapper.map(json) }
    }

    @Test
    fun `skal mappe json med mod dato fra gg arena formidlingsgruppe v1 til formidlingsgruppeEvent`() {
        val json = toJson("/kafka/formidlingsgruppe_med_mod_dato.json")
        val formidlingsgruppeEvent = FormidlingsgruppeMapper.map(json)
        assertThat(formidlingsgruppeEvent.foedselsnummer?.stringValue()).isEqualTo("***********")
        assertThat(formidlingsgruppeEvent.personId).isEqualTo("3226568")
        assertThat(formidlingsgruppeEvent.formidlingsgruppe).isEqualTo(Formidlingsgruppe("ARBS"))
        assertThat(formidlingsgruppeEvent.formidlingsgruppeEndret)
            .isEqualTo(LocalDateTime.of(2020, 6, 19, 9, 31, 50))
    }

    @Test
    fun `manglende fnr skal kaste exception`() {
        val json = toJson("/kafka/formidlingsgruppe_uten_fnr.json")
        assertThrows<IllegalArgumentException> { FormidlingsgruppeMapper.map(json) }
    }

    @Test
    fun `skal mappe både after og before`() {
        val json = toJson("/kafka/formidlingsgruppe_med_mod_dato.json")
        val formidlingsgruppeEvent = FormidlingsgruppeMapper.map(json)
        assertThat(formidlingsgruppeEvent.foedselsnummer?.stringValue()).isEqualTo("***********")
        assertThat(formidlingsgruppeEvent.personId).isEqualTo("3226568")
        assertThat(formidlingsgruppeEvent.operation).isEqualTo(Operation.UPDATE)
        assertThat(formidlingsgruppeEvent.formidlingsgruppe).isEqualTo(Formidlingsgruppe("ARBS"))
        assertThat(formidlingsgruppeEvent.formidlingsgruppeEndret)
            .isEqualTo(LocalDateTime.of(2020, 6, 19, 9, 31, 50))
        assertThat(formidlingsgruppeEvent.forrigeFormidlingsgruppe).isEqualTo(Formidlingsgruppe("ISERV"))
        assertThat(formidlingsgruppeEvent.forrigeFormidlingsgruppeEndret)
            .isEqualTo(LocalDateTime.of(2020, 6, 18, 11, 13, 1))
    }

    @Test
    fun `mapping av op type d for delete`() {
        val json = toJson("/kafka/formidlingsgruppe_op_type_D.json")
        val formidlingsgruppeEvent = FormidlingsgruppeMapper.map(json)
        assertThat(formidlingsgruppeEvent.foedselsnummer.stringValue()).isEqualTo("***********")
        assertThat(formidlingsgruppeEvent.personId).isEqualTo("1365747")
        assertThat(formidlingsgruppeEvent.operation).isEqualTo(Operation.DELETE)
        assertThat(formidlingsgruppeEvent.formidlingsgruppe).isEqualTo(Formidlingsgruppe("IJOBS"))
        assertThat(formidlingsgruppeEvent.formidlingsgruppeEndret)
            .isEqualTo(LocalDateTime.of(2016, 3, 12, 0, 47, 50))
    }
}
