package no.nav.fo.veilarbregistrering.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.fo.veilarbregistrering.FileToJson
import no.nav.fo.veilarbregistrering.bruker.pdl.endepunkt.PdlHentPersonResponse
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistrering
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class JsonDeserializationTest {


    private val test_json = "{\"sisteStilling\":{\"label\":\"Annen stilling\",\"styrk08\":\"-1\",\"konseptId\":-1},\"besvarelse\":{\"sisteStilling\":\"INGEN_SVAR\",\"utdanning\":\"INGEN_UTDANNING\",\"utdanningBestatt\":\"INGEN_SVAR\",\"utdanningGodkjent\":\"INGEN_SVAR\",\"dinSituasjon\":\"MISTET_JOBBEN\",\"helseHinder\":\"NEI\",\"andreForhold\":\"NEI\"},\"teksterForBesvarelse\":[{\"sporsmalId\":\"sisteStilling\",\"sporsmal\":\"Hva er din siste jobb?\",\"svar\":\"Annen stilling\"},{\"sporsmalId\":\"utdanning\",\"sporsmal\":\"Hva er din hÃ¸yeste fullfÃ¸rte utdanning?\",\"svar\":\"Ingen utdanning\"},{\"sporsmalId\":\"utdanningBestatt\",\"sporsmal\":\"Er utdanningen din bestÃ¥tt?\",\"svar\":\"Ikke aktuelt\"},{\"sporsmalId\":\"utdanningGodkjent\",\"sporsmal\":\"Er utdanningen din godkjent i Norge?\",\"svar\":\"Ikke aktuelt\"},{\"sporsmalId\":\"dinSituasjon\",\"sporsmal\":\"Velg den situasjonen som passer deg best\",\"svar\":\"Har mistet eller kommer til Ã¥ miste jobben\"},{\"sporsmalId\":\"helseHinder\",\"sporsmal\":\"Har du helseproblemer som hindrer deg i Ã¥ sÃ¸ke eller vÃ¦re i jobb?\",\"svar\":\"Nei\"},{\"sporsmalId\":\"andreForhold\",\"sporsmal\":\"Har du andre problemer med Ã¥ sÃ¸ke eller vÃ¦re i jobb?\",\"svar\":\"Nei\"}]}"

    private val mapper: ObjectMapper = jacksonObjectMapper().findAndRegisterModules()

    @Test
    fun `Deserialiserer dato i pdl-responser korrekt`() {
        val json = FileToJson.toJson("/pdl/hentPersonOk.json")

        val jacksonObject = mapper.readValue<PdlHentPersonResponse>(json)

        assertThat(jacksonObject.data.hentPerson.foedsel[0].foedselsdato).isEqualTo(LocalDate.of(2000,1, 1))
        assertThat(jacksonObject.data.hentPerson.adressebeskyttelse).isNotEmpty
        assertThat(jacksonObject.data.hentPerson.adressebeskyttelse[0].gradering.name).isEqualTo("STRENGT_FORTROLIG_UTLAND")
    }

    @Test
    fun `Tester json body fra frontend`() {
        val jacksonObject = mapper.readValue<OrdinaerBrukerRegistrering>(test_json)
        print(jacksonObject.besvarelse.sisteStilling)
    }
}