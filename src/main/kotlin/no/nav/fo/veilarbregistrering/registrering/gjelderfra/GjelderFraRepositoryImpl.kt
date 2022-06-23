package no.nav.fo.veilarbregistrering.registrering.gjelderfra

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.time.LocalDate
import java.time.LocalDateTime

class GjelderFraRepositoryImpl(private val db: NamedParameterJdbcTemplate): GjelderFraRepository {

    override fun opprettDatoFor(bruker: Bruker, brukerRegistreringId: Long, dato: LocalDate) {
//
//        val params = mapOf(
//            "foedselsnummer" to bruker.gjeldendeFoedselsnummer.foedselsnummer,
//            "dato" to dato,
//            "bruker_registrering_id" to brukerRegistreringId,
//        )
//
//        val sql = "INSERT INTO $GJELDER_FRA_DATO " +
//                " (${insertColumns.joinToString( ", ")})" +
//                " VALUES (:foedselsnummer, :dato, :bruker_registrering_id)"
//
//        db.update(sql, params)
        throw NotImplementedError()
    }
    private fun localDateTimeToLocalDate(localDateTime: LocalDateTime): LocalDate {
        return LocalDate.of(localDateTime.year, localDateTime.month, localDateTime.dayOfMonth)
    }

    override fun hentDatoFor(bruker: Bruker): GjelderFraDato? {
//        val sql = "SELECT * FROM $GJELDER_FRA_DATO where $FOEDSELSNUMMER = :id ORDER BY OPPRETTET_DATO DESC FETCH FIRST 1 ROWS ONLY"
//
//            return db.queryForObject(sql, mapOf("id" to bruker.aktorId)) { rs, _ ->
//                GjelderFraDato(
//                    id = rs.getLong(ID),
//                    foedselsnummer = Foedselsnummer(rs.getString(FOEDSELSNUMMER)),
//                    dato = localDateTimeToLocalDate(rs.getTimestamp(DATO).toLocalDateTime()),
//                    brukerRegistreringId = rs.getLong(BRUKER_REGISTRERING_ID),
//                    opprettetDato = rs.getTimestamp(OPPRETTET_DATO).toLocalDateTime()
//                )
//        }
        throw NotImplementedError()
    }

    companion object {
        private const val GJELDER_FRA_DATO = "GJELDER_FRA_DATO"
        const val ID = "ID"
        const val DATO = "DATO"
        const val FOEDSELSNUMMER = "FOEDSELSNUMMER"
        const val BRUKER_REGISTRERING_ID = "BRUKER_REGISTRERING_ID"
        const val OPPRETTET_DATO = "OPPRETTET_DATO"

        private val insertColumns = listOf(
            // ID, ??
            FOEDSELSNUMMER,
            DATO,
            BRUKER_REGISTRERING_ID,
            // OPPRETTET_DATO ?? - autogenerert?
        )

        private val allColumns = listOf(
            ID,
            FOEDSELSNUMMER,
            DATO,
            BRUKER_REGISTRERING_ID,
            OPPRETTET_DATO
        )
    }
}
