package no.nav.fo.veilarbregistrering.registrering.bruker.db;

import no.nav.fo.veilarbregistrering.db.DbIntegrasjonsTest;
import no.nav.fo.veilarbregistrering.registrering.bruker.*;
import no.nav.fo.veilarbregistrering.besvarelse.AndreForholdSvar;
import no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder;
import no.nav.fo.veilarbregistrering.besvarelse.TilbakeIArbeidSvar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;

import static no.nav.veilarbregistrering.db.DatabaseTestContext.setupInMemoryDatabaseContext;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BrukerRegistreringRepositoryDbIntegrationTest extends DbIntegrasjonsTest {

    private static final AktorId AKTOR_ID_11111 = AktorId.valueOf("11111");

    @Inject
    private JdbcTemplate jdbcTemplate;

    private BrukerRegistreringRepository brukerRegistreringRepository;


    @BeforeEach
    public void setup() {
        setupInMemoryDatabaseContext();
        brukerRegistreringRepository = new BrukerRegistreringRepositoryImpl(jdbcTemplate);
    }

    @Test
    public void registrerBruker() {
        OrdinaerBrukerRegistrering bruker = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering();
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository.lagreOrdinaerBruker(bruker, AKTOR_ID_11111);
        assertRegistrertBruker(bruker, ordinaerBrukerRegistrering);
    }

    @Test
    public void hentBrukerregistreringForAktorId() {

        OrdinaerBrukerRegistrering bruker1 = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering().setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse()
                .setAndreForhold(AndreForholdSvar.JA));
        OrdinaerBrukerRegistrering bruker2 = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering().setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse()
                .setAndreForhold(AndreForholdSvar.NEI));

        brukerRegistreringRepository.lagreOrdinaerBruker(bruker1, AKTOR_ID_11111);
        brukerRegistreringRepository.lagreOrdinaerBruker(bruker2, AKTOR_ID_11111);

        OrdinaerBrukerRegistrering registrering = brukerRegistreringRepository.hentOrdinaerBrukerregistreringForAktorId(AKTOR_ID_11111);
        assertRegistrertBruker(bruker2, registrering);
    }

    @Test
    public void hentSykmeldtregistreringForAktorId() {
        SykmeldtRegistrering bruker1 = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering().setBesvarelse(BesvarelseTestdataBuilder.gyldigSykmeldtSkalTilbakeSammeJobbBesvarelse()
                .setTilbakeIArbeid(TilbakeIArbeidSvar.JA_FULL_STILLING));
        SykmeldtRegistrering bruker2 = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering().setBesvarelse(BesvarelseTestdataBuilder.gyldigSykmeldtSkalTilbakeSammeJobbBesvarelse()
                .setTilbakeIArbeid(TilbakeIArbeidSvar.JA_REDUSERT_STILLING));

        brukerRegistreringRepository.lagreSykmeldtBruker(bruker1, AKTOR_ID_11111);
        brukerRegistreringRepository.lagreSykmeldtBruker(bruker2, AKTOR_ID_11111);

        SykmeldtRegistrering registrering = brukerRegistreringRepository.hentSykmeldtregistreringForAktorId(AKTOR_ID_11111);
        assertSykmeldtRegistrertBruker(bruker2, registrering);
    }

    @Test
    public void hentOrdinaerBrukerRegistreringForAktorId(){
        OrdinaerBrukerRegistrering bruker = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering().setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse()
                .setAndreForhold(AndreForholdSvar.JA));

        OrdinaerBrukerRegistrering lagretBruker = brukerRegistreringRepository.lagreOrdinaerBruker(bruker, AKTOR_ID_11111);
        bruker.setId(lagretBruker.getId()).setOpprettetDato(lagretBruker.getOpprettetDato());

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository
                .hentOrdinaerBrukerregistreringForAktorId(AKTOR_ID_11111);

        assertEquals(bruker, ordinaerBrukerRegistrering);

    }

    @Test
    public void hentOrdinaerBrukerRegistreringForAktorIdSkalReturnereNullHvisBrukerIkkeErRegistret(){
        AktorId uregistrertAktorId = AktorId.valueOf("9876543");
        OrdinaerBrukerRegistrering profilertBrukerRegistrering = brukerRegistreringRepository
                .hentOrdinaerBrukerregistreringForAktorId(uregistrertAktorId);

        assertNull(profilertBrukerRegistrering);
    }

    private void assertRegistrertBruker(OrdinaerBrukerRegistrering bruker, OrdinaerBrukerRegistrering ordinaerBrukerRegistrering) {
        assertThat(ordinaerBrukerRegistrering.getBesvarelse()).isEqualTo(bruker.getBesvarelse());
        assertThat(ordinaerBrukerRegistrering.getSisteStilling()).isEqualTo(bruker.getSisteStilling());
        assertThat(ordinaerBrukerRegistrering.getTeksterForBesvarelse()).isEqualTo(bruker.getTeksterForBesvarelse());
    }

    private void assertSykmeldtRegistrertBruker(SykmeldtRegistrering bruker, SykmeldtRegistrering sykmeldtRegistrering) {
        assertThat(sykmeldtRegistrering.getBesvarelse()).isEqualTo(bruker.getBesvarelse());
        assertThat(sykmeldtRegistrering.getTeksterForBesvarelse()).isEqualTo(bruker.getTeksterForBesvarelse());
    }
}
