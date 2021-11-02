package no.nav.fo.veilarbregistrering.bruker.krr

import no.nav.fo.veilarbregistrering.FileToJson.toJson
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.lang.RuntimeException
import kotlin.test.assertNotNull

class KrrClientTest {

    @Test
    fun `skal mappe kontaktinfo med mobiltelefonnummer`() {
        val json = toJson(OK_JSON)
        val foedselsnummer = Foedselsnummer.of("23067844532")
        val kontaktinfoDto = KrrClient.parse(json, foedselsnummer)
        assertNotNull(kontaktinfoDto)
        assertThat(kontaktinfoDto.mobiltelefonnummer).isEqualTo("11111111")
    }

    @Test
    fun `skal mappe feil til runtimeException`() {
        val json = toJson(FEIL_JSON)
        val foedselsnummer = Foedselsnummer.of("23067844539")
        val runtimeException = assertThrows(
            RuntimeException::class.java
        ) { KrrClient.parse(json, foedselsnummer) }
        assertThat(runtimeException.message)
            .isEqualTo("Henting av kontaktinfo fra KRR feilet: fant ikke person")
    }

    companion object {
        private const val OK_JSON = "/krr/hentKontaktinformasjonOk.json"
        private const val FEIL_JSON = "/krr/hentKontaktinformasjonError.json"
    }
}
