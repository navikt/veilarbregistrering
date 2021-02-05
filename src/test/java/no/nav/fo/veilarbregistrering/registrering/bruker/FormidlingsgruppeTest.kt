package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException

class FormidlingsgruppeTest {
    @Test
    fun null_skal_gi_illegalArgumentException() {
        assertThrows<IllegalArgumentException> { Formidlingsgruppe.of(null) }
    }

    @Test
    fun stringValue_for_nullableFormidlingsgruppe_skal_gi_null() {
        val formidlingsgruppe: Formidlingsgruppe = Formidlingsgruppe.nullable()
        Assertions.assertThat(formidlingsgruppe.stringValue()).isNull()
    }

    @Test
    fun value_for_nullableFormidlingsgruppe_skal_gi_null() {
        val formidlingsgruppe: Formidlingsgruppe = Formidlingsgruppe.nullable()
        Assertions.assertThat(formidlingsgruppe.value()).isEqualTo("INGEN_VERDI")
    }

    @Test
    fun formidlingsgruppe_for_nullableFormidlingsgruppe_skal_gi_null() {
        val formidlingsgruppe: Formidlingsgruppe = Formidlingsgruppe.nullable()
        Assertions.assertThat(formidlingsgruppe.fieldName()).isEqualTo("formidlingsgruppe")
    }
}