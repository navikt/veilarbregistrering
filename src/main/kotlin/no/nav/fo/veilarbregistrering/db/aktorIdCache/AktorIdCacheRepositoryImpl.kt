package no.nav.fo.veilarbregistrering.db.aktorIdCache

import no.nav.fo.veilarbregistrering.aktorIdCache.AktorIdCache
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.log.logger
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class AktorIdCacheRepositoryImpl(private val db: NamedParameterJdbcTemplate) {

    fun lagre(aktorIdCache: AktorIdCache){
        val params = mapOf(
            "foedselsnummer" to aktorIdCache.foedselsnummer,
            "aktor_id" to aktorIdCache.aktorId,
            "opprettet_dato" to aktorIdCache.opprettetDato
        )
        val sqlForInsert = "INSERT INTO aktor_id_cache (foedselsnummer, aktor_id, opprettet_dato) values (:foedselsnummer, :aktor_id, :opprettet_dato)"

        try {
            db.update(sqlForInsert, params)
        } catch (e: DataIntegrityViolationException) {
            logger.warn("Klarte ikke sette AktørId ${aktorIdCache.aktorId} inn i AktørId-cache", e)
        }
    }

    fun hentAktørId(fnr: Foedselsnummer): AktorIdCache? {
        val sql = "SELECT * FROM aktor_id_cache WHERE foedselsnummer = :fnr"
        val params = mapOf("fnr" to fnr.foedselsnummer)
         return db.queryForObject(sql, params, aktorIdCacheRowMapper)
    }

    companion object {
        private val aktorIdCacheRowMapper = RowMapper { rs, _ ->
            AktorIdCache(
                foedselsnummer = rs.getString("foedselsnummer"),
                aktorId = rs.getString("aktor_id"),
                opprettetDato = rs.getTimestamp("opprettet_dato").toLocalDateTime()
            )
        }
    }

}