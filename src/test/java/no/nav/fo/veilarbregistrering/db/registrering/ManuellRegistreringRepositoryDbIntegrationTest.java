package no.nav.fo.veilarbregistrering.db.registrering;

import no.nav.fo.veilarbregistrering.db.DbIntegrasjonsTest;
import no.nav.fo.veilarbregistrering.db.registrering.ManuellRegistreringRepositoryImpl;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistrering;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;

import static no.nav.veilarbregistrering.db.DatabaseTestContext.setupInMemoryDatabaseContext;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManuellRegistreringRepositoryDbIntegrationTest extends DbIntegrasjonsTest {

    @Inject
    private JdbcTemplate jdbcTemplate;

    private ManuellRegistreringRepositoryImpl manuellRegistreringRepository;

    @BeforeEach
    public void setup() {
        setupInMemoryDatabaseContext();
        manuellRegistreringRepository = new ManuellRegistreringRepositoryImpl(jdbcTemplate);
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
