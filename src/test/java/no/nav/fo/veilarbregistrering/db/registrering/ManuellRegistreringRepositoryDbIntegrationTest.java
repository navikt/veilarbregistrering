package no.nav.fo.veilarbregistrering.db.registrering;

import no.nav.fo.veilarbregistrering.db.MigrationUtils;
import no.nav.fo.veilarbregistrering.db.TransactionalTest;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistrering;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TransactionalTest
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
