package no.nav.fo.veilarbregistrering.registrering.veileder.db

import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType
import no.nav.fo.veilarbregistrering.registrering.veileder.ManuellRegistrering
import no.nav.fo.veilarbregistrering.registrering.veileder.ManuellRegistreringRepository
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.SQLException

class ManuellRegistreringRepositoryImpl(private val db: NamedParameterJdbcTemplate) : ManuellRegistreringRepository {
    override fun lagreManuellRegistrering(manuellRegistrering: ManuellRegistrering): Long {
        val id = nesteFraSekvens()
        val params = mapOf(
            "id" to id,
            "registrering_id" to manuellRegistrering.registreringId,
            "bruker_registrering_type" to manuellRegistrering.brukerRegistreringType.toString(),
            "veileder_ident" to manuellRegistrering.veilederIdent,
            "veileder_enhet_id" to manuellRegistrering.veilederEnhetId
        )

        val sql = "INSERT INTO $MANUELL_REGISTRERING" +
                " ($MANUELL_REGISTRERING_ID, $REGISTRERING_ID, $BRUKER_REGISTRERING_TYPE, $VEILEDER_IDENT, $VEILEDER_ENHET_ID)" +
                " VALUES (:id, :registrering_id, :bruker_registrering_type, :veileder_ident, :veileder_enhet_id)"

        db.update(sql, params)
        return id
    }

    override fun hentManuellRegistrering(
        registreringId: Long,
        brukerRegistreringType: BrukerRegistreringType
    ): ManuellRegistrering? {
        val params = mapOf(
            "id" to registreringId,
            "bruker_registrering_type" to brukerRegistreringType.toString()
        )

        val sql = "SELECT * FROM $MANUELL_REGISTRERING" +
                " WHERE $REGISTRERING_ID = :id AND $BRUKER_REGISTRERING_TYPE = :bruker_registrering_type" +
                " FETCH NEXT 1 ROWS ONLY"


        return db.query(sql, params, rowMapper).firstOrNull()
    }

    private fun nesteFraSekvens(): Long {
        val sql = "SELECT nextVal('$MANUELL_REGISTRERING_SEQ')"
        return db.queryForObject(sql, emptyMap<String, Any>(), Long::class.java)!!
    }

    companion object {
        private const val MANUELL_REGISTRERING_SEQ = "MANUELL_REGISTRERING_SEQ"
        private const val MANUELL_REGISTRERING = "MANUELL_REGISTRERING"
        const val MANUELL_REGISTRERING_ID = "MANUELL_REGISTRERING_ID"
        const val REGISTRERING_ID = "REGISTRERING_ID"
        const val BRUKER_REGISTRERING_TYPE = "BRUKER_REGISTRERING_TYPE"
        const val VEILEDER_IDENT = "VEILEDER_IDENT"
        const val VEILEDER_ENHET_ID = "VEILEDER_ENHET_ID"

        private val rowMapper = RowMapper<ManuellRegistrering> { rs, _ ->
            try {
                ManuellRegistrering(
                    rs.getLong(MANUELL_REGISTRERING_ID),
                    rs.getLong(REGISTRERING_ID),
                    BrukerRegistreringType.valueOf(rs.getString(BRUKER_REGISTRERING_TYPE)),
                    rs.getString(VEILEDER_IDENT),
                    rs.getString(VEILEDER_ENHET_ID),
                )
            } catch (e: SQLException) {
                throw RuntimeException(e)
            }
        }
    }
}