package no.nav.fo.veilarbregistrering.bruker.pdl

import io.mockk.mockk
import no.nav.common.json.JsonUtils
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.feil.BrukerIkkeFunnetException
import no.nav.fo.veilarbregistrering.bruker.pdl.hentGeografiskTilknytning.PdlGtType
import no.nav.fo.veilarbregistrering.bruker.pdl.hentGeografiskTilknytning.PdlHentGeografiskTilknytningRequest
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlGruppe
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlHentIdenterRequest
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdent
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.*
import org.approvaltests.Approvals
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import javax.inject.Provider
import javax.servlet.http.HttpServletRequest
import kotlin.test.assertEquals

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
        val pdlOppslagClient: PdlOppslagClient = object : PdlOppslagClient("", null) {
            public override fun hentPersonRequest(fnr: String, pdlHentPersonRequest: PdlHentPersonRequest): String {
                return toJson(HENT_PERSON_FEIL_JSON)
            }
        }
        assertThrows<RuntimeException> { pdlOppslagClient.hentPerson(AktorId.of("111lll")) }
    }

    @Test
    fun `skal feile ved not found`() {
        val pdlOppslagClient: PdlOppslagClient = object : PdlOppslagClient("", null) {
            public override fun hentPersonRequest(fnr: String, pdlHentPersonRequest: PdlHentPersonRequest): String {
                return toJson(HENT_PERSON_NOT_FOUND_JSON)
            }
        }
        assertThrows<BrukerIkkeFunnetException> {
            val pdlPerson = pdlOppslagClient.hentPerson(AktorId.of("111lll"))
            assertThat(pdlPerson).isNull()
        }
    }

    @Test
    fun `skal hente person`() {
        val pdlOppslagClient = object : PdlOppslagClient("", null) {
            public override fun hentPersonRequest(fnr: String, pdlHentPersonRequest: PdlHentPersonRequest) = toJson(HENT_PERSON_OK_JSON)
        }

        val (telefonnummer, foedsel, adressebeskyttelse) = pdlOppslagClient.hentPerson(AktorId.of("12345678910"))

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
    }


    @Test
    fun `skal hente identer til person`() {
        val client: PdlOppslagClient = object : PdlOppslagClient("", null) {
            public override fun hentIdenterRequest(personident: String, request: PdlHentIdenterRequest): String {
                return toJson(HENT_IDENTER_OK_JSON)
            }
        }
        val pdlIdenter = client.hentIdenter(Foedselsnummer.of("12345678910"))
        assertThat(pdlIdenter.identer).hasSize(2)
        assertTrue(pdlIdenter.identer.stream()
            .anyMatch { pdlIdent: PdlIdent -> pdlIdent.gruppe == PdlGruppe.FOLKEREGISTERIDENT && !pdlIdent.isHistorisk })
        assertTrue(pdlIdenter.identer.stream()
            .anyMatch { pdlIdent: PdlIdent -> pdlIdent.gruppe == PdlGruppe.AKTORID && !pdlIdent.isHistorisk })
    }

    @Test
    fun `skal hente identer med historikk til person`() {
        val client: PdlOppslagClient = object : PdlOppslagClient("", null) {
            public override fun hentIdenterRequest(personident: String, request: PdlHentIdenterRequest): String {
                return toJson(HENT_IDENTER_MED_HISTORISK_OK_JSON)
            }
        }
        val pdlIdenter = client.hentIdenter(Foedselsnummer.of("12345678910"))
        assertThat(pdlIdenter.identer).hasSize(3)
        assertTrue(pdlIdenter.identer.stream()
            .anyMatch { pdlIdent: PdlIdent -> pdlIdent.gruppe == PdlGruppe.FOLKEREGISTERIDENT && !pdlIdent.isHistorisk })
        assertTrue(pdlIdenter.identer.stream()
            .anyMatch { pdlIdent: PdlIdent -> pdlIdent.gruppe == PdlGruppe.AKTORID && !pdlIdent.isHistorisk })
        assertTrue(pdlIdenter.identer.stream()
            .anyMatch { pdlIdent: PdlIdent -> pdlIdent.gruppe == PdlGruppe.FOLKEREGISTERIDENT && pdlIdent.isHistorisk })
    }

    @Test
    fun `skal hente geografisk tilknytning til person`() {
        val client: PdlOppslagClient = object : PdlOppslagClient("", null) {
            public override fun hentGeografiskTilknytningRequest(
                fnr: String,
                pdlHentGeografiskTilknytningRequest: PdlHentGeografiskTilknytningRequest
            ): String {
                return toJson(HENT_GEOGRAFISK_TILKNYTNING_OK_JSON)
            }
        }

        val geografiskTilknytning = client.hentGeografiskTilknytning(AktorId.of("11123"))
        assertEquals(PdlGtType.BYDEL, geografiskTilknytning.gtType)
        assertEquals("030102", geografiskTilknytning.gtBydel)
    }

    private fun toJson(jsonFile: String) = Files.readString(Paths.get(PdlOppslagClient::class.java.getResource(jsonFile).toURI()), Charsets.UTF_8)

    companion object {
        private const val HENT_PERSON_OK_JSON = "/pdl/hentPersonOk.json"
        private const val HENT_PERSON_FEIL_JSON = "/pdl/hentPersonError.json"
        private const val HENT_PERSON_NOT_FOUND_JSON = "/pdl/hentPersonNotFound.json"
        private const val HENT_IDENTER_OK_JSON = "/pdl/hentIdenterOk.json"
        private const val HENT_IDENTER_MED_HISTORISK_OK_JSON = "/pdl/hentIdenterMedHistorikkOk.json"
        private const val HENT_GEOGRAFISK_TILKNYTNING_OK_JSON = "/pdl/hentGeografiskTilknytningOk.json"
    }
}
