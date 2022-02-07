package no.nav.fo.veilarbregistrering.enhet.adapter

import no.nav.fo.veilarbregistrering.FileToJson.toJson
import no.nav.fo.veilarbregistrering.enhet.adapter.EnhetRestClient.Companion.parse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EnhetRestClientTest {

    @Test
    fun `skal kunne parse json til organisasjonsdetaljer`() {
        val organisasjonDto = parse(toJson(OK_JSON))
        assertThat(organisasjonDto).isNotNull
        assertThat(organisasjonDto.organisasjonDetaljer).isNotNull
        assertThat(organisasjonDto.organisasjonDetaljer.forretningsadresser[0].kommunenummer)
            .isEqualTo("0301")
    }

    @Test
    fun `skal kunne parse json uten forretningsadresse til organisasjonsdetaljer`() {
        val organisasjonDto = parse(toJson(MANGLER_FORRETNINGSADRESSE_JSON))
        assertThat(organisasjonDto).isNotNull
        assertThat(organisasjonDto.organisasjonDetaljer).isNotNull
        assertThat(organisasjonDto.organisasjonDetaljer.forretningsadresser).isEmpty()
    }

    companion object {
        private const val OK_JSON = "/enhet/enhet.json"
        private const val MANGLER_FORRETNINGSADRESSE_JSON = "/enhet/enhetUtenForretningsadresse.json"
    }
}
