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
        val sql = "UPDATE $ARBEIDSSOKERPERIODE_TABELL SET til_og_med = :tilOgMed, overfort_kafka = FALSE WHERE til_og_med IS NULL AND foedselsnummer = :foedselsnummer"
        try {
            db.update(sql, params)
        } catch (e: DataIntegrityViolationException) {
            throw DataIntegrityViolationException("Avslutting av arbeidssøkerperiode feilet", e)
        }
    }

    override fun avsluttPeriode(id: Int, tilDato: LocalDateTime) {
        val params = mapOf(
            "id" to id,
            "tilOgMed" to tilDato
        )
        val sql = "UPDATE $ARBEIDSSOKERPERIODE_TABELL SET til_og_med = :tilOgMed, overfort_kafka = FALSE WHERE id = :id"

        try {
            db.update(sql, params)
        } catch (e: DataIntegrityViolationException) {
            throw DataIntegrityViolationException("Avslutning av arbeidssøkerperiode feilet", e)
        }
    }

    override fun hentPerioder(foedselsnummer: Foedselsnummer): List<ArbeidssokerperiodeDto> {
        val params = mapOf("foedselsnummer" to foedselsnummer.foedselsnummer)
        val sql = "SELECT * FROM $ARBEIDSSOKERPERIODE_TABELL WHERE foedselsnummer = :foedselsnummer ORDER BY id DESC"
        return db.query(sql, params, arbeidssokerperiodeMapper)
    }

    override fun hentPerioder(
        gjeldendeFoedselsnummer: Foedselsnummer,
        historiskeFoedselsnummer: List<Foedselsnummer>
    ): List<ArbeidssokerperiodeDto> {
        val foedselsnummerList = historiskeFoedselsnummer + gjeldendeFoedselsnummer
        val params = mapOf("foedselsnummerList" to foedselsnummerList.map(Foedselsnummer::stringValue))
        val sql = "SELECT * FROM $ARBEIDSSOKERPERIODE_TABELL WHERE foedselsnummer IN (:foedselsnummerList)"
        return db.query(sql, params, arbeidssokerperiodeMapper)
    }

    override fun hentNesteArbeidssokerperioder(antall: Int): List<ArbeidssokerperiodeDto> {
        val sql = "SELECT * FROM $ARBEIDSSOKERPERIODE_TABELL WHERE overfort_kafka IS FALSE ORDER BY fra_og_med LIMIT $antall"
        return db.query(sql, arbeidssokerperiodeMapper)
    }

    override fun settArbeidssokerperioderSomOverfort(listeMedIder: List<Int>) {
        val sql = "UPDATE $ARBEIDSSOKERPERIODE_TABELL SET overfort_kafka = TRUE WHERE id IN (:listeMedIder)"
        val params = mapOf("listeMedIder" to listeMedIder)

        db.update(sql, params)
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
