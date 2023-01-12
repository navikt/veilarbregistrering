package no.nav.fo.veilarbregistrering.db.aktorIdCache

import no.nav.fo.veilarbregistrering.aktorIdCache.AktorIdCache
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.db.arbeidssoker.FormidlingsgruppeRepositoryImpl
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.Timestamp
import java.time.LocalDateTime

class AktorIdCacheRepositoryImpl(private val db: NamedParameterJdbcTemplate) {
    fun lagre(aktorIdCache: AktorIdCache){
        val params = mapOf(
            "foedselsnummer" to aktorIdCache.foedselsnummer,
            "aktor_id" to aktorIdCache.aktor_id,
            "opprettet_dato" to aktorIdCache.opprettet_dato
        )
        val sqlForInsert = "INSERT INTO aktor_id_cache (foedselsnummer, aktor_id, opprettet_dato) values (:foedselsnummer, :aktor_id, :opprettet_dato)"
    }





}