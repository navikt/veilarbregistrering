package no.nav.fo.veilarbregistrering.enhet.adapter

import no.nav.fo.veilarbregistrering.FileToJson.toJson
import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer
import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer.Companion.of
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class EnhetGatewayTest {
    private lateinit var enhetGateway: EnhetGatewayImpl
    @BeforeEach
    fun setUp() {
        val enhetStubClient: EnhetRestClient = EnhetStubClient()
        enhetGateway = EnhetGatewayImpl(enhetStubClient)
    }

    @Test
    fun hentOrganisasjonsdetaljer_skal_kunne_hente_ut_kommunenummer_fra_enhetsregisteret() {
        val organisasjonsdetaljer = enhetGateway.hentOrganisasjonsdetaljer(of("995298775"))
        Assertions.assertThat(organisasjonsdetaljer).isNotNull
        Assertions.assertThat(organisasjonsdetaljer!!.kommunenummer()).hasValue(Kommunenummer.of("0301"))
    }

    @Test
    fun hentOrganisasjonsdetaljer_skal_gi_empty_result_ved_ukjent_org_nr() {
        val organisasjonsdetaljer = enhetGateway.hentOrganisasjonsdetaljer(of("123456789"))
        Assertions.assertThat(organisasjonsdetaljer).isNull()
    }

    @Test
    fun hentOrganisasjonsdetaljer_skal_kaste_runtimeException_ved_feil() {
        val runtimeException = assertThrows<RuntimeException> { enhetGateway.hentOrganisasjonsdetaljer(FEILENDE_ORG) }
        Assertions.assertThat(runtimeException.message).isEqualTo("Hent organisasjon feilet")
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