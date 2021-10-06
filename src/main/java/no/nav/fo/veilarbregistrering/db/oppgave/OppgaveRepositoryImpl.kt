package no.nav.fo.veilarbregistrering.db.oppgave

import no.nav.fo.veilarbregistrering.oppgave.OppgaveRepository
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.oppgave.OppgaveType
import no.nav.fo.veilarbregistrering.oppgave.OppgaveImpl
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.Timestamp
import java.time.LocalDateTime

class OppgaveRepositoryImpl(private val db: NamedParameterJdbcTemplate) : OppgaveRepository {
    override fun opprettOppgave(
        aktorId: AktorId,
        oppgaveType: OppgaveType,
        oppgaveId: Long
    ): Long {
        val id = nesteFraSekvens()
        val params  = mapOf(
            "id" to id,
            "aktor_id" to aktorId.asString(),
            "oppgavetype" to oppgaveType.name,
            "ekstern_oppgave_id" to oppgaveId,
            "opprettet" to Timestamp.valueOf(LocalDateTime.now())
        )

        val sql = "INSERT INTO $OPPGAVE" +
                " ($ID, $AKTOR_ID, $OPPGAVETYPE, $EKSTERN_OPPGAVE_ID, $OPPRETTET)" +
                " VALUES (:id, :aktor_id, :oppgavetype, :ekstern_oppgave_id, :opprettet)"

        db.update(sql, params)
        return id
    }

    private fun nesteFraSekvens(): Long {
        return db.queryForObject("SELECT OPPGAVE_SEQ.nextval FROM DUAL", emptyMap<String, Any>(), Long::class.java)!!
    }

    override fun hentOppgaverFor(aktorId: AktorId): List<OppgaveImpl> {
        val sql = "SELECT * FROM $OPPGAVE WHERE $AKTOR_ID = :aktor_id"
        return db.query(sql, mapOf("aktor_id" to aktorId.asString()), rowMapper)
    }

    companion object {
        private const val ID = "ID"
        private const val AKTOR_ID = "AKTOR_ID"
        private const val OPPGAVETYPE = "OPPGAVETYPE"
        private const val EKSTERN_OPPGAVE_ID = "EKSTERN_OPPGAVE_ID"
        private const val OPPRETTET = "OPPRETTET"
        private const val OPPGAVE = "OPPGAVE"

        private val rowMapper = RowMapper { rs, _ ->
            OppgaveImpl(
                rs.getLong(ID),
                AktorId(rs.getString(AKTOR_ID)),
                OppgaveType.valueOf(rs.getString(OPPGAVETYPE)),
                rs.getLong(EKSTERN_OPPGAVE_ID),
                rs.getTimestamp(OPPRETTET).toLocalDateTime()
            )
        }
    }
}