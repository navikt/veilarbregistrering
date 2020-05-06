package no.nav.fo.veilarbregistrering.db.profilering;

import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.db.profilering.ProfileringRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ProfileringRepositoryTest {
    
    private final static String BRUKER_REGISTRERING_ID = "BRUKER_REGISTRERING_ID";
    private final static String BRUKER_PROFILERING = "BRUKER_PROFILERING";
    private final static String PROFILERING_TYPE = "PROFILERING_TYPE";
    private final static String VERDI = "VERDI";
    private final static String ALDER = "ALDER";
    private final static String ARB_6_AV_SISTE_12_MND = "ARB_6_AV_SISTE_12_MND";
    private final static String RESULTAT_PROFILERING = "RESULTAT_PROFILERING";
    
    @Test
    public void profileringSkalSetteRiktigInformasjonIDatabase() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        ProfileringRepositoryImpl profileringRepository = new ProfileringRepositoryImpl(jdbcTemplate);

        Profilering profilering = new Profilering()
                .setInnsatsgruppe(Innsatsgruppe.STANDARD_INNSATS)
                .setJobbetSammenhengendeSeksAvTolvSisteManeder(true)
                .setAlder(36);
        long brukerregistreringId = 7258365L;

        profileringRepository.lagreProfilering(brukerregistreringId, profilering);

        String query = String.format("insert into %s (%s,%s,%s) values (?,?,?)", BRUKER_PROFILERING, BRUKER_REGISTRERING_ID, PROFILERING_TYPE, VERDI);

        verify(jdbcTemplate).update(query,brukerregistreringId, ALDER, profilering.getAlder());
        verify(jdbcTemplate).update(query, brukerregistreringId, ARB_6_AV_SISTE_12_MND, profilering.isJobbetSammenhengendeSeksAvTolvSisteManeder());
        verify(jdbcTemplate).update(query, brukerregistreringId, RESULTAT_PROFILERING, profilering.getInnsatsgruppe().getArenakode());
    }
}