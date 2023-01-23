package no.nav.fo.veilarbregistrering.bruker.pdl

import io.mockk.mockk
import no.nav.common.json.JsonUtils
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.feil.BrukerIkkeFunnetException
import no.nav.fo.veilarbregistrering.bruker.feil.PdlException
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.*
import org.approvaltests.Approvals
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import javax.inject.Provider
import javax.servlet.http.HttpServletRequest

class PdlOppslagClientTest {
    private lateinit var requestProvider: Provider<HttpServletRequest>

    @BeforeEach
    fun setUp() {
        requestProvider = mockk()
    }

    @Test
    fun `hentPersonVariables skal lage riktig JSON`() {
        val hentPersonVariables = HentPersonVariables("12345678910", false)
        Approvals.verify(JsonUtils.toJson(hentPersonVariables))
    }

    @Test
    fun `skal feile ved error`() {
        val pdlOppslagClient = object : PdlOppslagClient("", { TOKEN }) {
            override fun hentPersonRequest(fnr: String, pdlHentPersonRequest: PdlHentPersonRequest): String {
                return toJson(HENT_PERSON_FEIL_JSON)
            }
        }
        assertThrows<PdlException> { pdlOppslagClient.hentPerson(AktorId("111lll")) }
    }

    @Test
    fun `skal kunne hente ut detaljer om feil`() {
        val pdlOppslagClient = object : PdlOppslagClient("", { TOKEN }) {
            override fun hentGeografiskTilknytningRequest(
                fnr: String,
                pdlHentGeografiskTilknytningRequest: PdlHentGeografiskTilknytningRequest
            ) = toJson(HENT_GEOGRAFISK_TILKNYTNING_ERROR_JSON)
        }

        val feil = assertThrows<PdlException> { pdlOppslagClient.hentGeografiskTilknytning(AktorId("111lll")) }

        assertThat(feil.toString()).contains(
            "Feilmelding fra PDL",
            "hentGeografiskTilknytning",
            "1234",
            "cause-0001-manglerrolle",
            "feilkode_fra_pdl"
        )
    }

    @Test
    fun `skal feile ved not found`() {
        val pdlOppslagClient = object : PdlOppslagClient("", { TOKEN }) {
            override fun hentPersonRequest(fnr: String, pdlHentPersonRequest: PdlHentPersonRequest): String {
                return toJson(HENT_PERSON_NOT_FOUND_JSON)
            }
        }
        assertThrows<BrukerIkkeFunnetException> {
            val pdlPerson = pdlOppslagClient.hentPerson(AktorId("111lll"))
            assertThat(pdlPerson).isNull()
        }
    }

    @Test
    fun `skal hente person`() {
        val pdlOppslagClient = object : PdlOppslagClient("", { TOKEN }) {
            override fun hentPersonRequest(fnr: String, pdlHentPersonRequest: PdlHentPersonRequest) = toJson(HENT_PERSON_OK_JSON)
        }

        val (telefonnummer, foedsel, adressebeskyttelse, navn) = pdlOppslagClient.hentPerson(AktorId("12345678910"))

        assertThat(foedsel).isEqualTo(listOf(PdlFoedsel(
            foedselsdato = LocalDate.of(2000, 1, 1))
        ))
        assertThat(telefonnummer).isEqualTo(listOf(PdlTelefonnummer(
            landskode = "0047",
            nummer = "11223344",
            prioritet = 2
        )))
        assertThat(adressebeskyttelse).isEqualTo(listOf(PdlAdressebeskyttelse(
            gradering = PdlGradering.STRENGT_FORTROLIG_UTLAND
        )))

        assertThat(navn).isEqualTo(listOf(PdlNavn(fornavn = "Ola", mellomnavn = null, etternavn = "Normann")))
    }


    @Test
    fun `skal hente identer til person`() {
        val client = object : PdlOppslagClient("", { TOKEN }, { TOKEN }) {
            override fun hentIdenterRequest(personident: String, pdlHentIdenterRequest: PdlHentIdenterRequest, erSystemKontekst: Boolean): String {
                return toJson(HENT_IDENTER_OK_JSON)
            }
        }
        val pdlIdenter = client.hentIdenter(Foedselsnummer("12345678910"))
        assertThat(pdlIdenter.identer).hasSize(2)
        assertTrue(pdlIdenter.identer
            .any { pdlIdent: PdlIdent -> pdlIdent.gruppe == PdlGruppe.FOLKEREGISTERIDENT && !pdlIdent.historisk })
        assertTrue(pdlIdenter.identer
            .any { pdlIdent: PdlIdent -> pdlIdent.gruppe == PdlGruppe.AKTORID && !pdlIdent.historisk })
    }

    @Test
    fun `skal hente identer til person for systemkontekst`() {
        val client = object : PdlOppslagClient("", { TOKEN }, { SYSTEMTOKEN }) {
            override fun hentIdenterRequest(personident: String, pdlHentIdenterRequest: PdlHentIdenterRequest, erSystemKontekst: Boolean): String {
                return toJson(HENT_IDENTER_OK_JSON)
            }
        }
        val pdlIdenter = client.hentIdenterForSystemkontekst(Foedselsnummer("12345678910"))
        assertThat(pdlIdenter.identer).hasSize(2)
        assertTrue(pdlIdenter.identer
            .any { pdlIdent: PdlIdent -> pdlIdent.gruppe == PdlGruppe.FOLKEREGISTERIDENT && !pdlIdent.historisk })
        assertTrue(pdlIdenter.identer
            .any { pdlIdent: PdlIdent -> pdlIdent.gruppe == PdlGruppe.AKTORID && !pdlIdent.historisk })
    }

    @Test
    fun `skal hente identer med historikk til person`() {
        val client = object : PdlOppslagClient("", { TOKEN }, { TOKEN }) {
            override fun hentIdenterRequest(personident: String, pdlHentIdenterRequest: PdlHentIdenterRequest, erSystemKontekst: Boolean): String {
                return toJson(HENT_IDENTER_MED_HISTORISK_OK_JSON)
            }
        }
        val pdlIdenter = client.hentIdenter(Foedselsnummer("12345678910"))
        assertThat(pdlIdenter.identer).hasSize(3)
        assertTrue(pdlIdenter.identer
            .any { pdlIdent: PdlIdent -> pdlIdent.gruppe == PdlGruppe.FOLKEREGISTERIDENT && !pdlIdent.historisk })
        assertTrue(pdlIdenter.identer
            .any { pdlIdent: PdlIdent -> pdlIdent.gruppe == PdlGruppe.AKTORID && !pdlIdent.historisk })
        assertTrue(pdlIdenter.identer
            .any { pdlIdent: PdlIdent -> pdlIdent.gruppe == PdlGruppe.FOLKEREGISTERIDENT && pdlIdent.historisk })
    }

    @Test
    fun `skal hente geografisk tilknytning til person`() {
        val client = object : PdlOppslagClient("", { TOKEN }) {
            override fun hentGeografiskTilknytningRequest(
                fnr: String,
                pdlHentGeografiskTilknytningRequest: PdlHentGeografiskTilknytningRequest
            ): String {
                return toJson(HENT_GEOGRAFISK_TILKNYTNING_OK_JSON)
            }
        }

        val geografiskTilknytning = client.hentGeografiskTilknytning(AktorId("11123"))
        assertThat(geografiskTilknytning.gtType).isEqualTo(PdlGtType.BYDEL)
        assertThat(geografiskTilknytning.gtBydel).isEqualTo("030102")
    }

    private fun toJson(jsonFile: String) = Files.readString(Paths.get(PdlOppslagClient::class.java.getResource(jsonFile).toURI()), Charsets.UTF_8)

    companion object {
        private const val TOKEN = "Token"
        private const val SYSTEMTOKEN = "SystemToken"
        internal const val HENT_PERSON_OK_JSON = "/pdl/hentPersonOk.json"
        internal const val HENT_PERSON_FEIL_JSON = "/pdl/hentPersonError.json"
        internal const val HENT_PERSON_NOT_FOUND_JSON = "/pdl/hentPersonNotFound.json"
        internal const val HENT_IDENTER_OK_JSON = "/pdl/hentIdenterOk.json"
        internal const val HENT_IDENTER_MED_HISTORISK_OK_JSON = "/pdl/hentIdenterMedHistorikkOk.json"
        internal const val HENT_GEOGRAFISK_TILKNYTNING_OK_JSON = "/pdl/hentGeografiskTilknytningOk.json"
        internal const val HENT_GEOGRAFISK_TILKNYTNING_ERROR_JSON = "/pdl/hentGeografiskTilknytningError.json"
    }
}
