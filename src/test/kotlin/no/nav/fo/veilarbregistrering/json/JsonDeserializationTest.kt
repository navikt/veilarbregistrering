package no.nav.fo.veilarbregistrering.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.fo.veilarbregistrering.FileToJson
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.PdlHentPersonResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class JsonDeserializationTest {

    private val mapper: ObjectMapper = jacksonObjectMapper().findAndRegisterModules()

    @Test
    fun `Deserialiserer dato i pdl-responser korrekt`() {
        val json = FileToJson.toJson("/pdl/hentPersonOk.json")

        val jacksonObject = mapper.readValue<PdlHentPersonResponse>(json)

        assertThat(jacksonObject.data.hentPerson.foedsel[0].foedselsdato).isEqualTo(LocalDate.of(2000,1, 1))
        assertThat(jacksonObject.data.hentPerson.adressebeskyttelse).isNotEmpty
        assertThat(jacksonObject.data.hentPerson.adressebeskyttelse[0].gradering.name).isEqualTo("STRENGT_FORTROLIG_UTLAND")
    }
}