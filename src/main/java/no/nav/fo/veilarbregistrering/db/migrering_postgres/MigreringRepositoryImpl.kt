package no.nav.fo.veilarbregistrering.db.migrering_postgres

import no.nav.fo.veilarbregistrering.db.migrering_postgres.TabellNavn.BRUKER_PROFILERING
import no.nav.fo.veilarbregistrering.db.migrering_postgres.TabellNavn.BRUKER_REGISTRERING
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
            select 'oppgave', max(id), count(*) as row_count from oppgave
            """

        return db.queryForList(sql, emptyMap<String, Any>())
    }

    fun hentSjekksumFor(tabellNavn: TabellNavn): List<Map<String, Any>> {
        val sql = when (tabellNavn) {
            BRUKER_PROFILERING -> profileringSjekkSql
            BRUKER_REGISTRERING -> brukerRegistreringSjekkSql
            else -> "select 1"
        }
        return db.queryForList(sql, emptyMap<String, Any>())
    }

    companion object {
        private const val profileringSjekkSql = """
        select count(*) as antall_rader, count(distinct verdi) as unike_verdier, count(distinct profilering_type) as unike_typer 
        from bruker_profilering"            
        """

        private const val brukerRegistreringSjekkSql = """
        select count(*) as antall_rader, 
        count(distinct foedselsnummer) as unike_foedselsnummer, 
        count(distinct aktor_id) as unike_aktorer, 
        count(distinct jobbhistorikk) as unike_jobbhistorikk, 
        count(distinct yrkespraksis) as unike_yrkespraksis, 
        floor(avg(konsept_id)) as gjsnitt_konsept_id 
        from bruker_registrering
        """
    }
}

