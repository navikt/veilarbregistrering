package no.nav.fo.veilarbregistrering.bruker.pdl

import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.PdlIdent
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.PdlGruppe
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.PdlIdenter
import no.nav.fo.veilarbregistrering.bruker.Gruppe
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.PdlGradering
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.PdlAdressebeskyttelse
import no.nav.fo.veilarbregistrering.bruker.AdressebeskyttelseGradering
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.PdlPerson
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.PdlTelefonnummer
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.PdlFoedsel
import no.nav.fo.veilarbregistrering.bruker.Telefonnummer
import no.nav.fo.veilarbregistrering.bruker.Foedselsdato
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.PdlGeografiskTilknytning
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.PdlGtType
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PdlOppslagMapperTest {

    @Test
    fun `skal mappe identer`() {
        val pdlIdent = PdlIdent(ident = "12345678910", historisk = false, gruppe = PdlGruppe.FOLKEREGISTERIDENT)
        val pdlIdenter = PdlIdenter(listOf(pdlIdent))
        val identer = PdlOppslagMapper.map(pdlIdenter)
        assertThat(identer.identer).hasSize(1)
        assertThat(identer.identer[0].ident).isEqualTo("12345678910")
        assertThat(identer.identer[0].isHistorisk).isEqualTo(false)
        assertThat(identer.identer[0].gruppe).isEqualTo(Gruppe.FOLKEREGISTERIDENT)
    }

    @Test
    fun `skal mappe adressebeskyttelse`() {
        for (gradering in PdlGradering.values()) {
            val pdlAdressebeskyttelse = PdlAdressebeskyttelse(gradering)
            val mappedGradering = PdlOppslagMapper.map(pdlAdressebeskyttelse)
            assertThat(gradering.name).isEqualTo(mappedGradering.name)
        }
    }

    @Test
    fun `skal mappe person`() {
        val pdlPerson = PdlPerson(
            listOf(
                PdlTelefonnummer("11223344", "0043", 3),
                PdlTelefonnummer("11223344", "0041", 1),
                PdlTelefonnummer("11223344", "0042", 2)
            ),
            listOf(PdlFoedsel(LocalDate.of(1950, 12, 31))),
            emptyList()
        )
        val person = PdlOppslagMapper.map(pdlPerson)
        assertThat(person.harAdressebeskyttelse()).isFalse
        assertThat(person.telefonnummer).hasValue(Telefonnummer.of("11223344", "0041"))
        assertThat(person.foedselsdato).isEqualTo(Foedselsdato.of(LocalDate.of(1950, 12, 31)))
    }

    @Test
    fun `skal mappe person med adressebeskyttelse`() {
        val pdlPerson = PdlPerson(
            emptyList(),
            emptyList(),
            listOf(PdlAdressebeskyttelse(PdlGradering.STRENGT_FORTROLIG))
        )
        val person = PdlOppslagMapper.map(pdlPerson)
        assertThat(person.harAdressebeskyttelse()).isTrue
        assertThat(person.adressebeskyttelseGradering.erGradert()).isTrue
    }

    @Test
    fun `skal mappe til udefinert til null`() {
        val pdlGeografiskTilknytning = PdlGeografiskTilknytning(PdlGtType.UDEFINERT, null, null, null)
        val geografiskTilknytning = PdlOppslagMapper.map(pdlGeografiskTilknytning)
        assertThat(geografiskTilknytning).isNull()
    }

    @Test
    fun `skal mappe kommune nummer korrekt`() {
        val pdlGeografiskTilknytning = PdlGeografiskTilknytning(PdlGtType.KOMMUNE, "0144", "", "")
        val geografiskTilknytning = PdlOppslagMapper.map(pdlGeografiskTilknytning)
        assertThat(geografiskTilknytning).isEqualTo(GeografiskTilknytning.of("0144"))
    }

    @Test
    fun `skal mappe bydel nummer korrekt`() {
        val pdlGeografiskTilknytning = PdlGeografiskTilknytning(PdlGtType.BYDEL, null, "030102", null)
        val geografiskTilknytning = PdlOppslagMapper.map(pdlGeografiskTilknytning)
        assertThat(geografiskTilknytning).isEqualTo(GeografiskTilknytning.of("030102"))
    }

    @Test
    fun `skal mappe utland uten gtLand til nor`() {
        val pdlGeografiskTilknytning = PdlGeografiskTilknytning(PdlGtType.UTLAND, null, null, null)
        val geografiskTilknytning = PdlOppslagMapper.map(pdlGeografiskTilknytning)
        assertThat(geografiskTilknytning).isEqualTo(GeografiskTilknytning.of("NOR"))
    }

    @Test
    fun `skal mappe utland med gtLand til landkode`() {
        val pdlGeografiskTilknytning = PdlGeografiskTilknytning(PdlGtType.UTLAND, null, null, "POL")
        val geografiskTilknytning = PdlOppslagMapper.map(pdlGeografiskTilknytning)
        assertThat(geografiskTilknytning).isEqualTo(GeografiskTilknytning.of("POL"))
    }
}
