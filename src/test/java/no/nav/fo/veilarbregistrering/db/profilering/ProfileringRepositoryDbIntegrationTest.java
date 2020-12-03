package no.nav.fo.veilarbregistrering.db.profilering;

import no.nav.fo.veilarbregistrering.db.DatabaseConfig;
import no.nav.fo.veilarbregistrering.db.MigrationUtils;
import no.nav.fo.veilarbregistrering.db.RepositoryConfig;
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitConfig
@Transactional(transactionManager = "myTxMgr")
@ContextConfiguration(classes = {DatabaseConfig.class, RepositoryConfig.class})
public class ProfileringRepositoryDbIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProfileringRepository profileringRepository;

    @BeforeAll
    public void setup() {
        MigrationUtils.createTables(jdbcTemplate);
    }

    @Test
    public void profilerBruker() {
        Profilering profilering = new Profilering()
                .setAlder(39)
                .setJobbetSammenhengendeSeksAvTolvSisteManeder(true)
                .setInnsatsgruppe(Innsatsgruppe.BEHOV_FOR_ARBEIDSEVNEVURDERING);

        profileringRepository.lagreProfilering(9, profilering);
    }

}
