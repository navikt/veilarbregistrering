package no.nav.fo.veilarbregistrering.bruker.pdl

import io.mockk.mockk
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.feil.BrukerIkkeFunnetException
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlGruppe
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlHentIdenterRequest
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdent
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlHentPersonRequest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.Files
import java.nio.file.Paths
import javax.inject.Provider
import javax.servlet.http.HttpServletRequest

class PdlOppslagClientTest {
    private lateinit var requestProvider: Provider<HttpServletRequest>

    @BeforeEach
    fun setUp() {
        requestProvider = mockk()
    }

    @Test
    fun skalFeileVedError() {
        val pdlOppslagClient: PdlOppslagClient = object : PdlOppslagClient("", null) {
            public override fun hentPersonRequest(fnr: String, pdlHentPersonRequest: PdlHentPersonRequest): String {
                return toJson(HENT_PERSON_FEIL_JSON)
            }
        }
        assertThrows<RuntimeException> { pdlOppslagClient.hentPerson(AktorId.of("111lll")) }
    }

    @Test
    fun skalFeileVedNotFound() {
        val pdlOppslagClient: PdlOppslagClient = object : PdlOppslagClient("", null) {
            public override fun hentPersonRequest(fnr: String, pdlHentPersonRequest: PdlHentPersonRequest): String {
                return toJson(HENT_PERSON_NOT_FOUND_JSON)
            }
        }
        assertThrows<BrukerIkkeFunnetException> {
            val pdlPerson = pdlOppslagClient.hentPerson(AktorId.of("111lll"))
            Assertions.assertThat(pdlPerson).isNull()
        }
    }

    @Test
    fun skalHenteIdenterTilPerson() {
        val client: PdlOppslagClient = object : PdlOppslagClient("", null) {
            public override fun hentIdenterRequest(personident: String, request: PdlHentIdenterRequest): String {
                return toJson(HENT_IDENTER_OK_JSON)
            }
        }
        val pdlIdenter = client.hentIdenter(Foedselsnummer.of("12345678910"))
        Assertions.assertThat(pdlIdenter.identer).hasSize(2)
        assertTrue(pdlIdenter.identer.stream()
            .anyMatch { pdlIdent: PdlIdent -> pdlIdent.gruppe == PdlGruppe.FOLKEREGISTERIDENT && !pdlIdent.isHistorisk })
        assertTrue(pdlIdenter.identer.stream()
            .anyMatch { pdlIdent: PdlIdent -> pdlIdent.gruppe == PdlGruppe.AKTORID && !pdlIdent.isHistorisk })
    }

    @Test
    fun skalHenteIdenterMedHistorikkTilPerson() {
        val client: PdlOppslagClient = object : PdlOppslagClient("", null) {
            public override fun hentIdenterRequest(personident: String, request: PdlHentIdenterRequest): String {
                return toJson(HENT_IDENTER_MED_HISTORISK_OK_JSON)
            }
        }
        val pdlIdenter = client.hentIdenter(Foedselsnummer.of("12345678910"))
        Assertions.assertThat(pdlIdenter.identer).hasSize(3)
        assertTrue(pdlIdenter.identer.stream()
            .anyMatch { pdlIdent: PdlIdent -> pdlIdent.gruppe == PdlGruppe.FOLKEREGISTERIDENT && !pdlIdent.isHistorisk })
        assertTrue(pdlIdenter.identer.stream()
            .anyMatch { pdlIdent: PdlIdent -> pdlIdent.gruppe == PdlGruppe.AKTORID && !pdlIdent.isHistorisk })
        assertTrue(pdlIdenter.identer.stream()
            .anyMatch { pdlIdent: PdlIdent -> pdlIdent.gruppe == PdlGruppe.FOLKEREGISTERIDENT && pdlIdent.isHistorisk })
    }

    private fun toJson(json_file: String): String {
        return try {
            val bytes = Files.readAllBytes(Paths.get(PdlOppslagClient::class.java.getResource(json_file).toURI()))
            String(bytes)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    companion object {
        private const val HENT_PERSON_FEIL_JSON = "/pdl/hentPersonError.json"
        private const val HENT_PERSON_NOT_FOUND_JSON = "/pdl/hentPersonNotFound.json"
        private const val HENT_IDENTER_OK_JSON = "/pdl/hentIdenterOk.json"
        private const val HENT_IDENTER_MED_HISTORISK_OK_JSON = "/pdl/hentIdenterMedHistorikkOk.json"
    }
}