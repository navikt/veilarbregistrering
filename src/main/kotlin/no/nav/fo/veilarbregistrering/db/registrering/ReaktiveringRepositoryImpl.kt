package no.nav.fo.veilarbregistrering.db.registrering

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.registrering.reaktivering.Reaktivering
import no.nav.fo.veilarbregistrering.registrering.reaktivering.ReaktiveringRepository
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.SQLException
import java.sql.Timestamp
import java.time.LocalDateTime

class ReaktiveringRepositoryImpl(private val db: NamedParameterJdbcTemplate) : ReaktiveringRepository {

    override fun lagreReaktiveringForBruker(aktorId: AktorId) {
        val params = mapOf(
                "id" to nesteFraSekvens(BRUKER_REAKTIVERING_SEQ),
                "aktor_id" to aktorId.aktorId,
                "reaktivering_dato" to Timestamp.valueOf(LocalDateTime.now())
        )

        val sql = "INSERT INTO ${BRUKER_REAKTIVERING}" +
                "(${BRUKER_REAKTIVERING_ID}, ${BrukerRegistreringRepositoryImpl.AKTOR_ID}, ${REAKTIVERING_DATO})" +
                "VALUES (:id, :aktor_id, :reaktivering_dato)"

        db.update(sql, params)
    }

    private fun nesteFraSekvens(sekvensNavn: String): Long {
        return db.queryForObject("SELECT $sekvensNavn.nextval FROM DUAL", noParams, Long::class.java)!!
    }

    override fun finnReaktiveringer(aktorId: AktorId): List<Reaktivering> {
        val sql = "SELECT * FROM ${BRUKER_REAKTIVERING} WHERE ${BrukerRegistreringRepositoryImpl.AKTOR_ID} = :aktor_id"
        return db.query(sql, mapOf("aktor_id" to aktorId.aktorId), reaktiveringMapper)
    }

    companion object {
        private const val BRUKER_REAKTIVERING_SEQ = "BRUKER_REAKTIVERING_SEQ"
        private const val BRUKER_REAKTIVERING = "BRUKER_REAKTIVERING"
        private const val BRUKER_REAKTIVERING_ID = "BRUKER_REAKTIVERING_ID"
        private const val REAKTIVERING_DATO = "REAKTIVERING_DATO"

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