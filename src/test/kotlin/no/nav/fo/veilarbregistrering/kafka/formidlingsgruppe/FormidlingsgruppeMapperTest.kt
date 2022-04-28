package no.nav.fo.veilarbregistrering.kafka.formidlingsgruppe

import no.nav.fo.veilarbregistrering.FileToJson.toJson
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.arbeidssoker.Operation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.assertNull

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
        assertThat(formidlingsgruppeEvent.formidlingsgruppe).isEqualTo(Formidlingsgruppe.ARBEIDSSOKER)
        assertThat(formidlingsgruppeEvent.formidlingsgruppeEndret)
            .isEqualTo(LocalDateTime.of(2020, 6, 19, 9, 31, 50))
    }

    @Test
    fun `manglende fnr`() {
        val json = toJson("/kafka/formidlingsgruppe_uten_fnr.json")
        val formidlingsgruppeEvent = FormidlingsgruppeMapper.map(json)
        assertNull(formidlingsgruppeEvent.foedselsnummer)
        assertThat(formidlingsgruppeEvent.personId).isEqualTo("1652")
        assertThat(formidlingsgruppeEvent.operation).isEqualTo(Operation.INSERT)
        assertThat(formidlingsgruppeEvent.formidlingsgruppe).isEqualTo(Formidlingsgruppe.IKKE_SERVICEBRUKER)
        assertThat(formidlingsgruppeEvent.formidlingsgruppeEndret)
            .isEqualTo(LocalDateTime.of(2007, 12, 3, 3, 5, 54))
    }

    @Test
    fun `skal mappe både after og before`() {
        val json = toJson("/kafka/formidlingsgruppe_med_mod_dato.json")
        val formidlingsgruppeEvent = FormidlingsgruppeMapper.map(json)
        assertThat(formidlingsgruppeEvent.foedselsnummer?.stringValue()).isEqualTo("***********")
        assertThat(formidlingsgruppeEvent.personId).isEqualTo("3226568")
        assertThat(formidlingsgruppeEvent.operation).isEqualTo(Operation.UPDATE)
        assertThat(formidlingsgruppeEvent.formidlingsgruppe).isEqualTo(Formidlingsgruppe.ARBEIDSSOKER)
        assertThat(formidlingsgruppeEvent.formidlingsgruppeEndret)
            .isEqualTo(LocalDateTime.of(2020, 6, 19, 9, 31, 50))
        assertThat(formidlingsgruppeEvent.forrigeFormidlingsgruppe).isEqualTo(Formidlingsgruppe.IKKE_SERVICEBRUKER)
        assertThat(formidlingsgruppeEvent.forrigeFormidlingsgruppeEndret)
            .isEqualTo(LocalDateTime.of(2020, 6, 18, 11, 13, 1))
    }

    @Test
    fun `mapping av op type d for delete`() {
        val json = toJson("/kafka/formidlingsgruppe_op_type_D.json")
        val formidlingsgruppeEvent = FormidlingsgruppeMapper.map(json)
        assertThat(formidlingsgruppeEvent.foedselsnummer).isNull()
        assertThat(formidlingsgruppeEvent.personId).isEqualTo("1365747")
        assertThat(formidlingsgruppeEvent.operation).isEqualTo(Operation.DELETE)
        assertThat(formidlingsgruppeEvent.formidlingsgruppe).isEqualTo(Formidlingsgruppe.IKKE_ARBEIDSSØKER)
        assertThat(formidlingsgruppeEvent.formidlingsgruppeEndret)
            .isEqualTo(LocalDateTime.of(2016, 3, 12, 0, 47, 50))
    }
}
