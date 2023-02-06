package no.nav.fo.veilarbregistrering.db.aktorIdCache

import no.nav.fo.veilarbregistrering.aktorIdCache.AktorIdCache
import no.nav.fo.veilarbregistrering.aktorIdCache.AktorIdCacheRepository
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.log.logger
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class AktorIdCacheRepositoryImpl(private val db: NamedParameterJdbcTemplate): AktorIdCacheRepository {

    override fun lagre(aktorIdCache: AktorIdCache) {
        val params = mapOf(
            "foedselsnummer" to aktorIdCache.foedselsnummer.foedselsnummer,
            "aktor_id" to aktorIdCache.aktorId.aktorId,
            "opprettet_dato" to aktorIdCache.opprettetDato
        )
        val sqlForInsert = "INSERT INTO aktor_id_cache (foedselsnummer, aktor_id, opprettet_dato) values (:foedselsnummer, :aktor_id, :opprettet_dato)" +
                " on conflict do nothing"

        try {
            db.update(sqlForInsert, params)
        } catch (e: DataIntegrityViolationException) {
            logger.warn("Klarte ikke sette AktørId ${aktorIdCache.aktorId} inn i AktørId-cache", e)
        }
    }

    override fun hentAktørId(fnr: Foedselsnummer): AktorIdCache? {
        val sql = "SELECT * FROM aktor_id_cache WHERE foedselsnummer = :fnr"
        val params = mapOf("fnr" to fnr.foedselsnummer)
        return db.query(sql, params, aktorIdCacheRowMapper).firstOrNull()
    }

    companion object {
        private val aktorIdCacheRowMapper = RowMapper { rs, _ ->
            AktorIdCache(
                foedselsnummer = Foedselsnummer(rs.getString("foedselsnummer")),
                aktorId = AktorId(rs.getString("aktor_id")),
                opprettetDato = rs.getTimestamp("opprettet_dato").toLocalDateTime()
            )
        }
    }

}