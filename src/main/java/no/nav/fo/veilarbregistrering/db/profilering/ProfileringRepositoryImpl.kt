package no.nav.fo.veilarbregistrering.db.profilering

import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe
import no.nav.fo.veilarbregistrering.profilering.Profilering
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet
import java.sql.SQLException

class ProfileringRepositoryImpl(private val db: NamedParameterJdbcTemplate) : ProfileringRepository {
    override fun lagreProfilering(brukerregistreringId: Long, profilering: Profilering) {
        val params1 = mapOf(
            "bruker_registrering_id" to brukerregistreringId,
            "type" to ALDER,
            "verdi" to profilering.alder
        )
        val params2 = mapOf(
            "bruker_registrering_id" to brukerregistreringId,
            "type" to ARB_6_AV_SISTE_12_MND,
            "verdi" to profilering.isJobbetSammenhengendeSeksAvTolvSisteManeder
        )
        val params3 = mapOf(
            "bruker_registrering_id" to brukerregistreringId,
            "type" to RESULTAT_PROFILERING,
            "verdi" to profilering.innsatsgruppe.arenakode
        )

        val sql = "INSERT INTO $BRUKER_PROFILERING ($BRUKER_REGISTRERING_ID, $PROFILERING_TYPE, $VERDI)" +
                " VALUES (:bruker_registrering_id, :type, :verdi)"

        db.update(sql, params1)
        db.update(sql, params2)
        db.update(sql, params3)
    }

    override fun hentProfileringForId(brukerregistreringId: Long): Profilering =
        db.query(
            "SELECT * FROM $BRUKER_PROFILERING WHERE $BRUKER_REGISTRERING_ID = :id" +
                " FETCH NEXT 3 ROWS ONLY",
            mapOf("id" to brukerregistreringId), mapper)!!


    companion object {
        private const val BRUKER_REGISTRERING_ID = "BRUKER_REGISTRERING_ID"
        private const val BRUKER_PROFILERING = "BRUKER_PROFILERING"
        const val PROFILERING_TYPE = "PROFILERING_TYPE"
        const val VERDI = "VERDI"
        const val ALDER = "ALDER"
        const val ARB_6_AV_SISTE_12_MND = "ARB_6_AV_SISTE_12_MND"
        const val RESULTAT_PROFILERING = "RESULTAT_PROFILERING"

        private val mapper: (ResultSet) -> Profilering = { rs ->
            try {
                Profilering().also {
                    while (rs.next()) {
                        when (rs.getString(PROFILERING_TYPE)) {
                            ALDER -> it.alder = rs.getInt(VERDI)

                            ARB_6_AV_SISTE_12_MND -> it.isJobbetSammenhengendeSeksAvTolvSisteManeder =
                                rs.getBoolean(VERDI)

                            RESULTAT_PROFILERING -> it.innsatsgruppe = Innsatsgruppe.of(rs.getString(VERDI))
                        }
                    }
                }
            } catch (e: SQLException) {
                throw RuntimeException(e)
            }
        }
    }
}