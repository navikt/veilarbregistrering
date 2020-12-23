package no.nav.fo.veilarbregistrering.db.profilering

import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe
import no.nav.fo.veilarbregistrering.profilering.Profilering
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class ProfileringRepositoryTest {

    @Test
    fun `profilering skal sette riktig informasjon i database`() {
        val jdbcTemplate = Mockito.mock(NamedParameterJdbcTemplate::class.java)
        val profileringRepository = ProfileringRepositoryImpl(jdbcTemplate)
        val profilering = Profilering()
                .setInnsatsgruppe(Innsatsgruppe.STANDARD_INNSATS)
                .setJobbetSammenhengendeSeksAvTolvSisteManeder(true)
                .setAlder(36)
        val brukerregistreringId = 7258365L
        profileringRepository.lagreProfilering(brukerregistreringId, profilering)
        val query = "INSERT INTO $BRUKER_PROFILERING ($BRUKER_REGISTRERING_ID, $PROFILERING_TYPE, $VERDI)" +
                " VALUES (:bruker_registrering_id, :type, :verdi)"
        val idParam = mapOf("bruker_registrering_id" to brukerregistreringId)

        verify(jdbcTemplate).update(query, idParam + mapOf("type" to ALDER, "verdi" to profilering.alder))
        verify(jdbcTemplate).update(
            query,
            idParam + mapOf(
                "type" to ARB_6_AV_SISTE_12_MND,
                "verdi" to profilering.isJobbetSammenhengendeSeksAvTolvSisteManeder
            )
        )
        verify(jdbcTemplate).update(
            query,
            idParam + mapOf("type" to RESULTAT_PROFILERING, "verdi" to profilering.innsatsgruppe.arenakode)
        )
    }

    companion object {
        private const val BRUKER_REGISTRERING_ID = "BRUKER_REGISTRERING_ID"
        private const val BRUKER_PROFILERING = "BRUKER_PROFILERING"
        private const val PROFILERING_TYPE = "PROFILERING_TYPE"
        private const val VERDI = "VERDI"
        private const val ALDER = "ALDER"
        private const val ARB_6_AV_SISTE_12_MND = "ARB_6_AV_SISTE_12_MND"
        private const val RESULTAT_PROFILERING = "RESULTAT_PROFILERING"
    }
}