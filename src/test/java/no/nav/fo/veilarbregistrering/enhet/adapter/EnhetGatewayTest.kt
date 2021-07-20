package no.nav.fo.veilarbregistrering.enhet.adapter

import no.nav.fo.veilarbregistrering.FileToJson.toJson
import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer
import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer.Companion.of
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EnhetGatewayTest {
    private lateinit var enhetGateway: EnhetGatewayImpl

    @BeforeEach
    fun setUp() {
        val enhetStubClient: EnhetRestClient = EnhetStubClient()
        enhetGateway = EnhetGatewayImpl(enhetStubClient)
    }

    @Test
    fun `hentOrganisasjonsdetaljer skal kunne hente ut kommunenummer fra enhetsregisteret`() {
        val organisasjonsdetaljer = enhetGateway.hentOrganisasjonsdetaljer(of("995298775"))
        assertThat(organisasjonsdetaljer).isNotNull
        assertThat(organisasjonsdetaljer!!.kommunenummer()).hasValue(Kommunenummer.of("0301"))
    }

    @Test
    fun `hentOrganisasjonsdetaljer skal gi empty result ved ukjent org nr`() {
        val organisasjonsdetaljer = enhetGateway.hentOrganisasjonsdetaljer(of("123456789"))
        assertThat(organisasjonsdetaljer).isNull()
    }

    @Test
    fun `hentOrganisasjonsdetaljer skal kaste runtimeException ved feil`() {
        val runtimeException = assertThrows<RuntimeException> { enhetGateway.hentOrganisasjonsdetaljer(FEILENDE_ORG) }
        assertThat(runtimeException.message).isEqualTo("Hent organisasjon feilet")
    }

    companion object {
        private val FEILENDE_ORG: Organisasjonsnummer = of("0")
    }

    private class EnhetStubClient : EnhetRestClient("foo") {
        private val jsonResponse: MutableMap<Organisasjonsnummer, String> = HashMap()

        override fun hentOrganisasjon(organisasjonsnummer: Organisasjonsnummer): OrganisasjonDetaljerDto? {
            if (organisasjonsnummer.asString() == "0") throw RuntimeException("Hent organisasjon feilet")
            val jsonResponse = jsonResponse[organisasjonsnummer]
            if (jsonResponse == null) {
                println("Stub: Fant ikke organisasjonsnummer")
                return null
            }
            return parse(jsonResponse).organisasjonDetaljer
        }

        companion object {
            private const val OK_JSON = "/enhet/enhet.json"
        }

        init {
            jsonResponse[of("995298775")] = toJson(OK_JSON)
        }
    }
}
