package no.nav.fo.veilarbregistrering.bruker.krr

import no.nav.fo.veilarbregistrering.FileToJson.toJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class DigDirKrrProxyClientTest {

    @Test
    fun `skal mappe kontaktinfo med mobiltelefonnummer med ny jsonparser`() {
        val json = toJson(OK_JSON)
        val kontaktinfoDto = DigDirKrrProxyClient.parse(json)
        assertNotNull(kontaktinfoDto)
        assertThat(kontaktinfoDto.mobiltelefonnummer).isEqualTo("22222222")
    }

    companion object {
        private const val OK_JSON = "/krr/hentKrrOk.json"
    }
}
