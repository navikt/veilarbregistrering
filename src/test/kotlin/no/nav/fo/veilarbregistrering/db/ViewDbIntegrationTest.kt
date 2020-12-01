package no.nav.fo.veilarbregistrering.db

import no.nav.json.JsonUtils
import org.assertj.core.api.Assertions
import org.json.JSONArray
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.springframework.jdbc.core.JdbcTemplate
import java.util.*
import javax.inject.Inject

@RunWith(Parameterized::class)
class ViewDbIntegrationTest(private val viewName: String) : DbIntegrasjonsTest() {

    @Inject
    private lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun `database skal ha riktig antall views`() {
        val count = jdbcTemplate.queryForList(
                "SELECT " +
                "COUNT(*) AS VIEW_COUNT " +
                "FROM INFORMATION_SCHEMA.VIEWS;"
        )[0]["VIEW_COUNT"] as Long
        Assertions.assertThat(count).isEqualTo(antallViews.toLong())
    }

    @Test
    fun `view eksisterer`() {
        val viewData = jdbcTemplate.queryForList("SELECT * FROM $viewName;")
        Assertions.assertThat(viewData).isNotNull
    }

    @Test
    fun `view skal reflektere kolonner i tabell`() {
        val kolonneData = jsonFormatter(JsonUtils.toJson(hentKolonneDataForView(viewName)))
        val kolonneDataFasit = jsonFormatter(lesInnholdFraFil("view-meta-data/" + viewName.toLowerCase() + ".json"))
        Assertions.assertThat(kolonneData).isEqualTo(kolonneDataFasit)
    }

    private fun hentKolonneDataForView(view: String): List<Map<String, Any>> {
        return jdbcTemplate.queryForList(
                "SELECT " +
                        "COLUMN_NAME, " +
                        "TYPE_NAME, " +
                        "CHARACTER_MAXIMUM_LENGTH " +
                        "FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_NAME = '$view';"
        )
    }

    companion object {
        @Parameterized.Parameters
        @JvmStatic
        fun views(): List<String> {
            return listOf(
                    "DVH_BRUKER_REGISTRERING",
                    "DVH_BEGRUNNELSE_KODEVERK",
                    "DVH_BRUKER_PROFILERING",
                    "DVH_BRUKER_REAKTIVERING",
                    "DVH_PROFILERING_KODEVERK",
                    "DVH_BRUKER_REGISTRERING_TEKST",
                    "DVH_SYKMELDT_REGISTRERING",
                    "DVH_SYKMELDT_REG_TEKST",
                    "DVH_SITUASJON_KODEVERK",
                    "DVH_TILBAKE_KODEVERK"
            )
        }

        private val antallViews = views().size

        private fun jsonFormatter(jsonArray: String): String {
            return JSONArray(jsonArray).toString()
        }

        private fun lesInnholdFraFil(filNavn: String): String {
            return Scanner(ViewDbIntegrationTest::class.java.classLoader.getResourceAsStream(filNavn), "UTF-8").useDelimiter("\\A").next()
        }
    }
}