package no.nav.fo.veilarbregistrering.db.migrering_postgres

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

enum class TabellNavn(val idKolonneNavn: String) {
    BRUKER_REGISTRERING("BRUKER_REGISTRERING_ID"),
    BRUKER_PROFILERING("BRUKER_REGISTRERING_ID"),
    BRUKER_REAKTIVERING("BRUKER_REAKTIVERING_ID"),
    SYKMELDT_REGISTRERING("SYKMELDT_REGISTRERING_ID"),
    MANUELL_REGISTRERING("MANUELL_REGISTRERING_ID"),
    REGISTRERING_TILSTAND("ID"),
    OPPGAVE("ID"),
    FORMIDLINGSGRUPPE("ID"),
    BEGRUNNELSE_KODEVERK("BEGRUNNELSE_ID")
}

class MigreringRepositoryImpl(private val db: NamedParameterJdbcTemplate) {

    fun nesteFraTabell(tabellNavn: TabellNavn, id: Long): List<Map<String, Any>> {
            val sql = "SELECT * " +
                    "FROM ${tabellNavn.name} " +
                    "WHERE ${tabellNavn.idKolonneNavn} > :id " +
                    "ORDER BY ${tabellNavn.idKolonneNavn} " +
                    "FETCH NEXT 1000 ROWS ONLY"
            return db.queryForList(sql, mapOf("id" to id))
    }
}

