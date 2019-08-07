package no.nav.fo.veilarbregistrering.registrering.manuell;

import no.nav.fo.veilarbregistrering.db.IntegrasjonsTest;
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistrering;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;

import static no.nav.veilarbregistrering.db.DatabaseTestContext.setupInMemoryDatabaseContext;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManuellRegistreringRepositoryIntegrationTest extends IntegrasjonsTest {

    @Inject
    private JdbcTemplate jdbcTemplate;

    private ManuellRegistreringRepository manuellRegistreringRepository;

    @BeforeEach
    public void setup() {
        setupInMemoryDatabaseContext();
        manuellRegistreringRepository = new ManuellRegistreringRepository(jdbcTemplate);
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
