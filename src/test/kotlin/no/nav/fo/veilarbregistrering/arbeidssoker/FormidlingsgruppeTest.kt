package no.nav.fo.veilarbregistrering.arbeidssoker

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertNotNull

class FormidlingsgruppeTest {

    @ParameterizedTest
    @ValueSource(strings = ["ARBS", "ISERV", "IARBS"])

    fun `skal_kunne_utlede_formidlingsgruppe_ut_fra_kode`(kode : String) {
        val formidlingsgruppe = Formidlingsgruppe.valueOfKode(kode)
        assertNotNull(formidlingsgruppe)
    }
}