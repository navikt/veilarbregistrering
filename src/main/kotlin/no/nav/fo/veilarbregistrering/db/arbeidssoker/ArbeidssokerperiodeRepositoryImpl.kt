package no.nav.fo.veilarbregistrering.db.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeDto
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeRepository
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.Timestamp
import java.time.LocalDateTime

class ArbeidssokerperiodeRepositoryImpl(private val db: NamedParameterJdbcTemplate) : ArbeidssokerperiodeRepository {
    override fun startPeriode(foedselsnummer: Foedselsnummer, fraDato: LocalDateTime) {
        val params = mapOf(
            "foedselsnummer" to foedselsnummer.foedselsnummer,
            "fraOgMed" to fraDato
        )

        val sql =
            """INSERT INTO $ARBEIDSSOKERPERIODE_TABELL("foedselsnummer", "fra_og_med") VALUES (:foedselsnummer, :fraOgMed)""".trimMargin()

        try {
            db.update(sql, params)
        } catch (e: DataIntegrityViolationException) {
            throw DataIntegrityViolationException("Lagring av følgende arbeidssøkerperiode feilet", e)
        }
    }

    override fun avsluttPeriode(foedselsnummer: Foedselsnummer, tilDato: LocalDateTime) {
        val params = mapOf(
            "tilOgMed" to tilDato,
            "foedselsnummer" to foedselsnummer.foedselsnummer
        )
        val sql = "UPDATE $ARBEIDSSOKERPERIODE_TABELL SET til_og_med = :tilOgMed WHERE til_og_med IS NULL AND foedselsnummer = :foedselsnummer"
        try {
            db.update(sql, params)
        } catch (e: DataIntegrityViolationException) {
            throw DataIntegrityViolationException("Avslutting av arbeidssøkerperiode feilet", e)
        }
    }

    override fun lagrePeriode(foedselsnummer: Foedselsnummer, fraDato: LocalDateTime, tilDato: LocalDateTime?) {
        val params = mapOf(
            "fraOgMed" to fraDato,
            "tilOgMed" to tilDato,
            "foedselsnummer" to foedselsnummer.foedselsnummer
        )

        val sql =
            """INSERT INTO $ARBEIDSSOKERPERIODE_TABELL("foedselsnummer", "fra_og_med", "til_og_med") VALUES (:foedselsnummer, :fraOgMed, :tilOgMed)""".trimMargin()

        try {
            db.update(sql, params)
        } catch (e: DataIntegrityViolationException) {
            throw DataIntegrityViolationException("Lagring av følgende arbeidssøkerperiode feilet", e)
        }
    }

    override fun hentPerioder(foedselsnummer: Foedselsnummer): List<ArbeidssokerperiodeDto> {
        val params = mapOf("foedselsnummer" to foedselsnummer.foedselsnummer)
        val sql = "SELECT * FROM $ARBEIDSSOKERPERIODE_TABELL WHERE foedselsnummer = :foedselsnummer ORDER BY id DESC"
        return db.query(sql, params, arbeidssokerperiodeMapper)
    }

    companion object {
        const val ARBEIDSSOKERPERIODE_TABELL = "arbeidssokerperiode"
        private val arbeidssokerperiodeMapper = RowMapper { rs, _ ->
            ArbeidssokerperiodeDto(
                rs.getInt("id"),
                Foedselsnummer(rs.getString("foedselsnummer")),
                rs.getTimestamp("fra_og_med").toLocalDateTime(),
                rs.getTimestamp("til_og_med")?.let(Timestamp::toLocalDateTime)
            )
        }
    }
}
