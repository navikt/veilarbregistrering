package no.nav.fo.veilarbregistrering.db

import no.nav.common.json.JsonUtils
import org.assertj.core.api.Assertions.assertThat
import org.json.JSONArray
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import java.util.*

@JdbcTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration( classes = [ RepositoryConfig::class, DatabaseConfig::class ])
open class ViewDbIntegrationTest {

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun setup() {
        MigrationUtils.createTables(jdbcTemplate)
    }

    @Test
    fun `database skal ha riktig antall views`() {
        val count = jdbcTemplate.queryForList(
                "SELECT " +
                        "COUNT(*) AS VIEW_COUNT " +
                        "FROM INFORMATION_SCHEMA.VIEWS;"
        )[0]["VIEW_COUNT"] as Long
        assertThat(count).isEqualTo(antallViews.toLong())
    }

    @ParameterizedTest
    @MethodSource("viewsForTest")
    fun `view eksisterer`(viewName: String) {
        val viewData = jdbcTemplate.queryForList("SELECT * FROM $viewName;")
        assertThat(viewData).isNotNull
    }

    @ParameterizedTest
    @MethodSource("viewsForTest")
    fun `view skal reflektere kolonner i tabell`(viewName: String) {
        val kolonneData = jsonFormatter(JsonUtils.toJson(hentKolonneDataForView(viewName)))
        val kolonneDataFasit = jsonFormatter(lesInnholdFraFil("view-meta-data/" + viewName.toLowerCase() + ".json"))
        assertThat(kolonneData).isEqualTo(kolonneDataFasit)
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

        @JvmStatic
        fun viewsForTest() = listOf(
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

        private val antallViews = viewsForTest().count()

        private fun jsonFormatter(jsonArray: String): String {
            return JSONArray(jsonArray).toString()
        }

        private fun lesInnholdFraFil(filNavn: String): String {
            return Scanner(ViewDbIntegrationTest::class.java.classLoader.getResourceAsStream(filNavn)!!, "UTF-8").useDelimiter("\\A").next()
        }
    }
}