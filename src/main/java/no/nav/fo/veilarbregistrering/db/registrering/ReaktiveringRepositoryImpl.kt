package no.nav.fo.veilarbregistrering.db.registrering

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.registrering.bruker.ReaktiveringRepository
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.Timestamp
import java.time.LocalDateTime

class ReaktiveringRepositoryImpl(private val db: NamedParameterJdbcTemplate) : ReaktiveringRepository {

    override fun lagreReaktiveringForBruker(aktorId: AktorId) {
        val params = mapOf(
                "id" to nesteFraSekvens(BRUKER_REAKTIVERING_SEQ),
                "aktor_id" to aktorId.asString(),
                "reaktivering_dato" to Timestamp.valueOf(LocalDateTime.now())
        )

        val sql = "INSERT INTO ${BRUKER_REAKTIVERING}" +
                "(${BRUKER_REAKTIVERING_ID}, ${AKTOR_ID}, ${REAKTIVERING_DATO})" +
                "VALUES (:id, :aktor_id, :reaktivering_dato)"

        db.update(sql, params)
    }

    private fun nesteFraSekvens(sekvensNavn: String): Long {
        return db.queryForObject("SELECT $sekvensNavn.nextval FROM DUAL", noParams, Long::class.java)!!
    }

    companion object {
        private const val AKTOR_ID = "AKTOR_ID"

        private const val BRUKER_REAKTIVERING_SEQ = "BRUKER_REAKTIVERING_SEQ"
        private const val BRUKER_REAKTIVERING_ID = "BRUKER_REAKTIVERING_ID"
        private const val BRUKER_REAKTIVERING = "BRUKER_REAKTIVERING"
        private const val REAKTIVERING_DATO = "REAKTIVERING_DATO"

        private val noParams = emptyMap<String, Any>()
    }
}