package no.nav.fo.veilarbregistrering.db.registrering

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.config.isOnPrem
import no.nav.fo.veilarbregistrering.registrering.reaktivering.Reaktivering
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringRepository
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.SQLException
import java.sql.Timestamp
import java.time.LocalDateTime

class ReaktiveringRepositoryImpl(private val db: NamedParameterJdbcTemplate) : ReaktiveringRepository {

    override fun lagreReaktiveringForBruker(bruker: Bruker): Long {
        val id = nesteFraSekvens(BRUKER_REAKTIVERING_SEQ)
        val params = mapOf(
                "id" to id,
                "aktor_id" to bruker.aktorId.aktorId,
                "foedselsnummer" to bruker.gjeldendeFoedselsnummer.foedselsnummer,
                "reaktivering_dato" to Timestamp.valueOf(LocalDateTime.now())
        )

        val sql = "INSERT INTO ${BRUKER_REAKTIVERING}" +
                "($BRUKER_REAKTIVERING_ID, $AKTOR_ID, $FOEDSELSNUMMER, $REAKTIVERING_DATO)" +
                "VALUES (:id, :aktor_id, :foedselsnummer, :reaktivering_dato)"

        db.update(sql, params)
        return id
    }

    private fun nesteFraSekvens(sekvensNavn: String): Long {
        val sql = if (isOnPrem()) "SELECT $sekvensNavn.nextval FROM DUAL" else "SELECT nextVal('$sekvensNavn')"
        return db.queryForObject(sql, noParams, Long::class.java)!!
    }

    override fun finnReaktiveringer(aktorId: AktorId): List<Reaktivering> {
        val sql = "SELECT * FROM ${BRUKER_REAKTIVERING} WHERE ${BrukerRegistreringRepositoryImpl.AKTOR_ID} = :aktor_id"
        return db.query(sql, mapOf("aktor_id" to aktorId.aktorId), reaktiveringMapper)
    }

    override fun finnAktorIdTilRegistrertUtenFoedselsnummer(maksAntall: Int, aktorIdDenyList: List<AktorId>): List<AktorId> {
        val sql = if (aktorIdDenyList.isEmpty()) {
            "SELECT DISTINCT $AKTOR_ID FROM $BRUKER_REAKTIVERING " +
                    "WHERE $FOEDSELSNUMMER IS NULL LIMIT $maksAntall"
        } else {
            "SELECT DISTINCT $AKTOR_ID FROM $BRUKER_REAKTIVERING " +
                    "WHERE $FOEDSELSNUMMER IS NULL AND $AKTOR_ID NOT IN (${aktorIdDenyList.joinToString(",") { t -> "'" + t.aktorId + "'" }}) LIMIT $maksAntall"
        }
        return db.query(sql) { rs, _ -> AktorId(rs.getString(AKTOR_ID)) }
    }

    override fun oppdaterRegistreringerMedManglendeFoedselsnummer(aktorIdFoedselsnummerMap: Map<AktorId, Foedselsnummer>): IntArray {
        val sql = "UPDATE $BRUKER_REAKTIVERING SET $FOEDSELSNUMMER = :foedselsnummer WHERE $AKTOR_ID = :aktorId AND $FOEDSELSNUMMER IS NULL"

        val params = aktorIdFoedselsnummerMap.map { aktorIdFoedselsnummer ->
            mapOf(
                "aktorId" to aktorIdFoedselsnummer.key.aktorId,
                "foedselsnummer" to aktorIdFoedselsnummer.value.foedselsnummer)
        }

        return db.batchUpdate(sql, params.toTypedArray())
    }

    companion object {
        private const val BRUKER_REAKTIVERING_SEQ = "BRUKER_REAKTIVERING_SEQ"
        private const val BRUKER_REAKTIVERING = "BRUKER_REAKTIVERING"
        private const val BRUKER_REAKTIVERING_ID = "BRUKER_REAKTIVERING_ID"
        private const val REAKTIVERING_DATO = "REAKTIVERING_DATO"
        private const val AKTOR_ID = "AKTOR_ID"
        private const val FOEDSELSNUMMER = "FOEDSELSNUMMER"

        private val noParams = emptyMap<String, Any>()

        private val reaktiveringMapper = RowMapper<Reaktivering> { rs, _ ->
            try {
                Reaktivering(
                        rs.getLong("BRUKER_REAKTIVERING_ID"),
                        AktorId(rs.getString("AKTOR_ID")),
                        rs.getTimestamp("reaktivering_dato").toLocalDateTime())
            } catch (e: SQLException) {
                throw RuntimeException(e)
            }
        }
    }
}