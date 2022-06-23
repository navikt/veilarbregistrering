package no.nav.fo.veilarbregistrering.db.gjelderfra

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.registrering.gjelderfra.GjelderFraDato
import no.nav.fo.veilarbregistrering.registrering.gjelderfra.GjelderFraRepository
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime

class GjelderFraRepositoryImpl(private val db: NamedParameterJdbcTemplate): GjelderFraRepository {

    override fun opprettDatoFor(bruker: Bruker, brukerRegistreringId: Long, dato: LocalDate) {
        val id = nesteFraSekvens()
        val params = mapOf(
            "id" to id,
            "foedselsnummer" to bruker.gjeldendeFoedselsnummer.foedselsnummer,
            "dato" to dato,
            "bruker_registrering_id" to brukerRegistreringId,
            "opprettet_dato" to Timestamp.valueOf(LocalDateTime.now())
        )

        val sql = "INSERT INTO $GJELDER_FRA_DATO " +
                " (${allColumns.joinToString( ", ")})" +
                " VALUES (:id, :foedselsnummer, :dato, :bruker_registrering_id, :opprettet_dato)"

        db.update(sql, params)
    }

    override fun hentDatoFor(bruker: Bruker): GjelderFraDato? {
        val sql = "SELECT * FROM $GJELDER_FRA_DATO where $FOEDSELSNUMMER = :foedselsnummer ORDER BY OPPRETTET_DATO DESC FETCH FIRST 1 ROWS ONLY"

            return db.query(sql, mapOf("foedselsnummer" to bruker.gjeldendeFoedselsnummer.foedselsnummer)) { rs, _ ->
                GjelderFraDato(
                    id = rs.getLong(ID),
                    foedselsnummer = Foedselsnummer(rs.getString(FOEDSELSNUMMER)),
                    dato = rs.getDate(DATO).toLocalDate(),
                    brukerRegistreringId = rs.getLong(BRUKER_REGISTRERING_ID),
                    opprettetDato = rs.getTimestamp(OPPRETTET_DATO).toLocalDateTime()
                )
        }.firstOrNull()
    }

    private fun nesteFraSekvens(): Long {
        return db.queryForObject("SELECT $GJELDER_FRA_DATO_SEQ.nextval FROM DUAL", emptyMap<String, Any>(), Long::class.java)!!
    }

    companion object {
        private const val GJELDER_FRA_DATO_SEQ = "GJELDER_FRA_DATO_SEQ"
        private const val GJELDER_FRA_DATO = "GJELDER_FRA_DATO"
        const val ID = "ID"
        const val DATO = "DATO"
        const val FOEDSELSNUMMER = "FOEDSELSNUMMER"
        const val BRUKER_REGISTRERING_ID = "BRUKER_REGISTRERING_ID"
        const val OPPRETTET_DATO = "OPPRETTET_DATO"

        private val allColumns = listOf(
            ID,
            FOEDSELSNUMMER,
            DATO,
            BRUKER_REGISTRERING_ID,
            OPPRETTET_DATO
        )
    }
}
