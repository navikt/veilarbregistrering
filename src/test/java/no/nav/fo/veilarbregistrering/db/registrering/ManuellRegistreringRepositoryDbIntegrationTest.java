package no.nav.fo.veilarbregistrering.db.registrering;

import no.nav.fo.veilarbregistrering.db.DatabaseConfig;
import no.nav.fo.veilarbregistrering.db.MigrationUtils;
import no.nav.fo.veilarbregistrering.db.RepositoryConfig;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistrering;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitConfig
@Transactional(transactionManager = "myTxMgr")
@ContextConfiguration(classes = {DatabaseConfig.class, RepositoryConfig.class})
public class ManuellRegistreringRepositoryDbIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ManuellRegistreringRepository manuellRegistreringRepository;

    @BeforeAll
    public void setup() {
        MigrationUtils.createTables(jdbcTemplate);
    }

    @Test
    public void hentManuellRegistrering(){

        String veilederIdent = "Z1234567";
        String veilederEnhetId = "1234";
        long registreringId = 1;
        BrukerRegistreringType registreringType = BrukerRegistreringType.ORDINAER;

        ManuellRegistrering manuellRegistrering = new ManuellRegistrering()
                .setRegistreringId(registreringId)
                .setBrukerRegistreringType(registreringType)
                .setVeilederIdent(veilederIdent)
                .setVeilederEnhetId(veilederEnhetId);

        long id = manuellRegistreringRepository.lagreManuellRegistrering(manuellRegistrering);

        manuellRegistrering.setId(id);

        ManuellRegistrering hentetRegistrering = manuellRegistreringRepository
                .hentManuellRegistrering(registreringId, registreringType);

        assertEquals(manuellRegistrering, hentetRegistrering);

    }

}
