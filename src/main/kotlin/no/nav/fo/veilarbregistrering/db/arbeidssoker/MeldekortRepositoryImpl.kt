package no.nav.fo.veilarbregistrering.db.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortPeriode
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.MeldekortRepository
import no.nav.fo.veilarbregistrering.arbeidssoker.meldekort.Meldekorttype
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.Timestamp

class MeldekortRepositoryImpl(private val db: NamedParameterJdbcTemplate) : MeldekortRepository {

    override fun lagre(meldekort: MeldekortEvent): Long {
        val id = nesteFraSekvens()
        val params = mapOf(
            "id" to id,
            "foedselsnummer" to meldekort.fnr.foedselsnummer,
            "er_arbeidssoker_neste_periode" to meldekort.erArbeidssokerNestePeriode,
            "periode_fra" to meldekort.nåværendePeriode.periodeFra,
            "periode_til" to meldekort.nåværendePeriode.periodeTil,
            "meldekorttype" to meldekort.meldekorttype.name,
            "meldekort_event_id" to meldekort.meldekortEventId,
            "event_opprettet" to Timestamp.valueOf(meldekort.eventOpprettet)
        )

        val sql = """
            INSERT INTO
                $MELDEKORT_TABELL (
                    "id",
                    "foedselsnummer",
                    "er_arbeidssoker_neste_periode",
                    "periode_fra",
                    "periode_til",
                    "meldekorttype",
                    "meldekort_event_id",
                    "event_opprettet"
                )
                VALUES (
                  :id,
                  :foedselsnummer,
                  :er_arbeidssoker_neste_periode,
                  :periode_fra,
                  :periode_til,
                  :meldekorttype,
                  :meldekort_event_id,
                  :event_opprettet
                )
            """
        try {
            db.update(sql, params)
        } catch (e: DataIntegrityViolationException) {
            throw DataIntegrityViolationException("Lagring av følgende meldekort feilet: $meldekort", e)
        }
        return id
    }

    override fun hent(foedselsnummer: Foedselsnummer): List<MeldekortEvent> {
        val params = mapOf("foedselsnummer" to foedselsnummer.foedselsnummer)
        val sql = "SELECT * FROM $MELDEKORT_TABELL WHERE foedselsnummer = :foedselsnummer"
        val meldekort = db.query(sql, params, meldekortEventMapper)
        return meldekort
    }

    private fun nesteFraSekvens(): Long {
        val sql = "SELECT nextVal('meldekort_seq')"
        return db.queryForObject(sql, emptyMap<String, Any>(), Long::class.java)!!
    }

    companion object {
        const val MELDEKORT_TABELL = "MELDEKORT"
        private val meldekortEventMapper = RowMapper { rs, _ ->
            MeldekortEvent(
                Foedselsnummer(rs.getString("foedselsnummer")),
                rs.getBoolean("er_arbeidssoker_neste_periode"),
                MeldekortPeriode(
                    rs.getDate("periode_fra").toLocalDate(),
                    rs.getDate("periode_til").toLocalDate(),
                    ),
                Meldekorttype.from(rs.getString("meldekorttype")),
                rs.getLong("meldekort_event_id"),
                rs.getTimestamp("event_opprettet").toLocalDateTime()
            )
        }
    }
}