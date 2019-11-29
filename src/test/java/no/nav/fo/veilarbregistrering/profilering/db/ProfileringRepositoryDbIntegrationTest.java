package no.nav.fo.veilarbregistrering.profilering.db;

import no.nav.fo.veilarbregistrering.db.DbIntegrasjonsTest;
import no.nav.fo.veilarbregistrering.profilering.Innsatsgruppe;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;

import static no.nav.veilarbregistrering.db.DatabaseTestContext.setupInMemoryDatabaseContext;

public class ProfileringRepositoryDbIntegrationTest extends DbIntegrasjonsTest {

    @Inject
    private JdbcTemplate jdbcTemplate;

    private ProfileringRepository profileringRepository;

    @BeforeEach
    public void setup() {
        setupInMemoryDatabaseContext();
        profileringRepository = new ProfileringRepositoryImpl(jdbcTemplate);
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
