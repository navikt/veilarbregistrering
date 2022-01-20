package no.nav.fo.veilarbregistrering.bruker.krr

import io.mockk.every
import io.mockk.mockk
import no.nav.common.featuretoggle.UnleashClient
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.Telefonnummer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KrrGatewayTest {

    @Test
    fun `skal hente kontaktinfo fra krr`() {
        val unleashClient: UnleashClient = mockk()
        every { unleashClient.isEnabled(any()) } returns true

        val krrGateway = KrrGatewayImpl(StubKrrClient(), StubDigDirKrrProxyClient(), unleashClient)

        val telefonnummer = krrGateway.hentKontaktinfo(Bruker(IDENT, AktorId("1234")))

        assertThat(telefonnummer).isEqualTo(Telefonnummer.of("23235656"))
    }

    private class StubKrrClient : KrrClient("", mockk()) {
        override fun hentKontaktinfo(foedselsnummer: Foedselsnummer): KrrKontaktInfo? {
            return KrrKontaktInfo(true, "23235656", false)
        }
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