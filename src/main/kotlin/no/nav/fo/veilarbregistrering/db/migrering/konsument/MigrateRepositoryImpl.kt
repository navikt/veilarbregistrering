package no.nav.fo.veilarbregistrering.db.migrering.konsument

import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.migrering.TabellNavn
import no.nav.fo.veilarbregistrering.migrering.TabellNavn.*
import no.nav.fo.veilarbregistrering.migrering.konsument.MigrateRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstand
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp
import java.time.ZonedDateTime

class MigrateRepositoryImpl(private val db: NamedParameterJdbcTemplate) : MigrateRepository {

    override fun hentStoersteId(tabellNavn: TabellNavn): Int {

        val rowSet = db.jdbcTemplate.queryForRowSet(
            "select ${tabellNavn.idKolonneNavn} " +
                    "from ${tabellNavn.name} " +
                    "where ${tabellNavn.idKolonneNavn} < 10000000" +
                    "order by ${tabellNavn.idKolonneNavn} desc limit 1"
        )

        return when (rowSet.next()) {
            true -> {
                val id = rowSet.getInt(tabellNavn.idKolonneNavn)

                /* I dette tilfellet (bruker_profilering) har man 3 rader per id (bruker_registrering_id).
                Må starte fra forrige id dersom vi ikke har et "komplett sett" */
                if (tabellNavn == BRUKER_PROFILERING) {
                    val resultat = db.jdbcTemplate.queryForRowSet("select count(*) as $ANTALL from ${tabellNavn.name} where ${tabellNavn.idKolonneNavn} = $id")
                    val raderMedSisteId = if (resultat.next()) resultat.getInt(ANTALL) else 0

                    return if (raderMedSisteId < 3) {
                        logger.info("Fant $raderMedSisteId rader for Id: [${id}]")
                        id - 1
                    } else {
                        logger.info("Fant $raderMedSisteId rader for Id: [${id}]")
                        id
                    }
                }
                id
            }
            false -> 0
        }
    }


    override fun settInnRader(tabell: TabellNavn, rader: List<MutableMap<String, Any>>) {
        try {
            if (rader.isEmpty()) return

            // Behandle kolonner vi vet må konverteres
            when (tabell) {
                BRUKER_REGISTRERING, SYKMELDT_REGISTRERING -> {
                    rader.forEach {
                        it["OPPRETTET_DATO"] = ZonedDateTime.parse(it["OPPRETTET_DATO"].toString()).toLocalDateTime()
                    }
                }

                BRUKER_REAKTIVERING -> {
                    rader.forEach {
                        it["REAKTIVERING_DATO"] =
                            ZonedDateTime.parse(it["REAKTIVERING_DATO"].toString()).toLocalDateTime()
                    }
                }

                REGISTRERING_TILSTAND -> {
                    rader.forEach {
                        it["OPPRETTET"] = ZonedDateTime.parse(it["OPPRETTET"].toString()).toLocalDateTime()
                        if (it["SIST_ENDRET"] != null) it["SIST_ENDRET"] =
                            ZonedDateTime.parse(it["SIST_ENDRET"].toString()).toLocalDateTime()
                    }
                }

                OPPGAVE -> {
                    rader.forEach {
                        it["OPPRETTET"] = ZonedDateTime.parse(it["OPPRETTET"].toString()).toLocalDateTime()
                    }
                }

                BRUKER_PROFILERING, MANUELL_REGISTRERING -> {
                }
            }

            // Bygg opp en (Java Persistence API) SQL string for den gitte tabellen
            val jpaSQL =
                if (tabell == BRUKER_PROFILERING)
                    """
            INSERT INTO bruker_profilering (${rader[0].keys.joinToString(postfix = "", prefix = "", separator = ",")}) 
            VALUES(${rader[0].keys.joinToString(prefix = ":", postfix = "", separator = ", :")})
            ON CONFLICT (bruker_registrering_id, profilering_type)
            DO NOTHING
            """
                else
                    """
            INSERT INTO ${tabell.name} (${rader[0].keys.joinToString(postfix = "", prefix = "", separator = ",")}) 
            VALUES(${rader[0].keys.joinToString(prefix = ":", postfix = "", separator = ", :")})
            """

            db.batchUpdate(jpaSQL, rader.toTypedArray())
            logger.info("Satt inn ${rader.size} rader i tabell ${tabell.name}")

        } catch (e: Exception) {
            logger.error(e.javaClass.name + ": " + e.message, e)
        }
    }

    override fun antallRaderSomKanTrengeOppdatering(): Int {
        return try {
            val sql = "select count(*) as antall from registrering_tilstand " +
                    "where status not in ('PUBLISERT_KAFKA', 'OPPRINNELIG_OPPRETTET_UTEN_TILSTAND') AND id < 10000000"
            db.queryForObject(sql, emptyMap<String, Any>()) { rs: ResultSet, _ ->
                rs.getInt("antall")
            }!!
        } catch (e: SQLException) {
            logger.error("Error counting number of potentially updated rows", e)
            0
        }
    }

    override fun hentRaderSomKanTrengeOppdatering(): List<RegistreringTilstand> {
        val sql = "select * from registrering_tilstand " +
                "where status not in ('PUBLISERT_KAFKA', 'OPPRINNELIG_OPPRETTET_UTEN_TILSTAND') AND id < 10000000 limit 1000"

        return db.query(sql, emptyMap<String, Any>(), registreringTilstandRowMapper)
    }

    override fun hentSjekkerForTabell(tabellNavn: TabellNavn): List<Map<String, Any>> {
        val sql = when (tabellNavn) {
            BRUKER_PROFILERING -> profileringSjekkSql
            BRUKER_REGISTRERING -> brukerRegistreringSjekkSql
            SYKMELDT_REGISTRERING -> sykmeldtRegistreringSjekkSql
            MANUELL_REGISTRERING -> manuellRegistreringSjekkSql
            OPPGAVE -> oppgaveSjekkSql
            BRUKER_REAKTIVERING -> brukerReaktiveringSjekkSql
            REGISTRERING_TILSTAND -> registreringstilstandSjekkSql
        }

        return db.jdbcTemplate.queryForList(sql)
    }

    override fun oppdaterTilstander(tilstander: List<Map<String, Any>>): List<Int> {
        val params = tilstander.map { tilstand ->
            mapOf(
                "id" to tilstand["ID"],
                "status" to tilstand["STATUS"],
                "sist_endret" to (tilstand["SIST_ENDRET"]?.let {
                    ZonedDateTime.parse(it.toString()).toLocalDateTime()
                }
                    ?: run {
                        logger.warn("Fant oppdatert tilstand uten SIST_ENDRET")
                        null
                    })

            )
        }

        val sql = "update registrering_tilstand set status = :status, sist_endret = :sist_endret where id = :id"

        logger.info("I ferd med å gjøre batch update for ${params.size} rader")
        return db.batchUpdate(sql, params.toTypedArray()).asList()
    }


    companion object {
        private const val ANTALL = "antall"

        private const val brukerReaktiveringSjekkSql = """
        select count(*) as antall_rader,
        count(distinct aktor_id) as unike_aktor_id
        from bruker_reaktivering
        """

        private const val registreringstilstandSjekkSql = """
        select count(*) as antall_rader,
        count(distinct bruker_registrering_id) as unike_brukerregistrering_id,
        floor(avg(bruker_registrering_id)) as gjsnitt_bruker_registrering_id
        from registrering_tilstand
        """
        private const val profileringSjekkSql = """
        select count(*) as antall_rader, count(distinct verdi) as unike_verdier, count(distinct profilering_type) as unike_typer 
        from bruker_profilering          
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

        private const val sykmeldtRegistreringSjekkSql = """
        select count(*) as antall_rader,
        count(distinct fremtidig_situasjon) as unike_fremtidig_situasjon,
        count(distinct aktor_id) as unike_aktorer,
        count(distinct utdanning_bestatt) as unike_utdanning_bestatt,
        count(distinct andre_utfordringer) as unike_andre_utfordringer,
        round(avg(cast(nus_kode as int)), 2) as gjsnitt_nus from sykmeldt_registrering
        """

        private const val manuellRegistreringSjekkSql = """
        select count(*) as antall_rader,
        count(distinct veileder_ident) as unike_veiledere,
        count(distinct veileder_enhet_id) as unike_enheter,
        count(distinct registrering_id) as unike_registreringer, 
        count(distinct bruker_registrering_type) as unike_reg_typer from manuell_registrering
        """

        private const val oppgaveSjekkSql = """
        select count(*) as antall_rader,
        count(distinct aktor_id) as unike_aktorer,
        count(distinct oppgavetype) as unike_oppgavetyper,
        count(distinct ekstern_oppgave_id) as unike_oppgave_id,
        floor(avg(ekstern_oppgave_id)) as gjsnitt_oppgave_id from oppgave
        """

        private val registreringTilstandRowMapper: RowMapper<RegistreringTilstand> = RowMapper { rs, _ ->
            RegistreringTilstand(
                rs.getLong(ID),
                rs.getLong(BRUKER_REGISTRERING_ID),
                rs.getTimestamp(OPPRETTET).toLocalDateTime(),
                rs.getTimestamp(SIST_ENDRET)
                    ?.let(Timestamp::toLocalDateTime),
                Status.valueOf(rs.getString(STATUS))
            )
        }

        const val ID = "ID"
        const val BRUKER_REGISTRERING_ID = "BRUKER_REGISTRERING_ID"
        const val OPPRETTET = "OPPRETTET"
        const val SIST_ENDRET = "SIST_ENDRET"
        const val STATUS = "STATUS"
    }
}
