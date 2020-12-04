package no.nav.fo.veilarbregistrering.db.profilering

import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe
import no.nav.fo.veilarbregistrering.profilering.Profilering
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.springframework.jdbc.core.JdbcTemplate

class ProfileringRepositoryTest {

    @Test
    fun `profilering skal sette riktig informasjon i database`() {
        val jdbcTemplate = Mockito.mock(JdbcTemplate::class.java)
        val profileringRepository = ProfileringRepositoryImpl(jdbcTemplate)
        val profilering = Profilering()
                .setInnsatsgruppe(Innsatsgruppe.STANDARD_INNSATS)
                .setJobbetSammenhengendeSeksAvTolvSisteManeder(true)
                .setAlder(36)
        val brukerregistreringId = 7258365L
        profileringRepository.lagreProfilering(brukerregistreringId, profilering)
        val query = String.format("insert into %s (%s,%s,%s) values (?,?,?)", BRUKER_PROFILERING, BRUKER_REGISTRERING_ID, PROFILERING_TYPE, VERDI)
        verify(jdbcTemplate).update(query, brukerregistreringId, ALDER, profilering.alder)
        verify(jdbcTemplate).update(query, brukerregistreringId, ARB_6_AV_SISTE_12_MND, profilering.isJobbetSammenhengendeSeksAvTolvSisteManeder)
        verify(jdbcTemplate).update(query, brukerregistreringId, RESULTAT_PROFILERING, profilering.innsatsgruppe.arenakode)
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