package no.nav.fo.veilarbregistrering.bruker.krr

import io.mockk.mockk
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Telefonnummer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KrrGatewayTest {

    @Test
    fun `skal hente kontaktinfo fra krr`() {
        val krrGateway = KrrGatewayImpl(StubDigDirKrrProxyClient())

        val telefonnummer = krrGateway.hentKontaktinfo(Bruker(IDENT, AktorId("1234")))

        assertThat(telefonnummer).isEqualTo(Telefonnummer.of("23235656"))
    }

    private class StubDigDirKrrProxyClient : DigDirKrrProxyClient("", mockk()) {
        override fun hentKontaktinfo(foedselsnummer: Foedselsnummer): DigDirKrrProxyResponse? {
            return DigDirKrrProxyResponse(foedselsnummer.stringValue(), true, false, "23235656")
        }
    }

    companion object {
        private val IDENT = Foedselsnummer.of("10108000398") //Aremark fiktivt fnr.";
    }
}