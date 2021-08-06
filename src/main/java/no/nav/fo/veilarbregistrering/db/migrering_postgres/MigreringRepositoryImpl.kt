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
}

class MigreringRepositoryImpl(private val db: NamedParameterJdbcTemplate) {

    fun nesteFraTabell(tabellNavn: TabellNavn, id: Long): List<Map<String, Any>> {
            val sql =
                """
                SELECT *
                FROM ${tabellNavn.name}
                WHERE ${tabellNavn.idKolonneNavn} > :id
                ORDER BY ${tabellNavn.idKolonneNavn}
                FETCH NEXT 1000 ROWS ONLY
                """
            return db.queryForList(sql, mapOf("id" to id))
    }

    fun hentStatus(): List<Map<String, Any>> {
        val sql =
            """
            select 'registrering_tilstand' as table_name, max(id), count(*) as row_count from registrering_tilstand union 
            select 'bruker_registrering', max(bruker_registrering_id), count(*) as row_count from bruker_registrering union
            select 'bruker_profilering', max(bruker_registrering_id), count(*) as row_count from bruker_profilering union
            select 'bruker_reaktivering', max(bruker_reaktivering_id), count(*) as row_count from bruker_reaktivering union
            select 'sykmeldt_registrering', max(sykmeldt_registrering_id), count(*) as row_count from sykmeldt_registrering union
            select 'manuell_registrering', max(manuell_registrering_id), count(*) as row_count from manuell_registrering union
            select 'oppgave', max(id), count(*) as row_count from oppgave;
            """

        return db.queryForList(sql, emptyMap<String, Any>())
    }
}

