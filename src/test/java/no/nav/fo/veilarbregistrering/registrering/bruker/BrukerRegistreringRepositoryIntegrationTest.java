package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.db.IntegrasjonsTest;
import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.AndreForholdSvar;
import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.TilbakeIArbeidSvar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;

import static no.nav.fo.veilarbregistrering.utils.TestUtils.*;
import static no.nav.veilarbregistrering.db.DatabaseTestContext.setupInMemoryDatabaseContext;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BrukerRegistreringRepositoryIntegrationTest extends IntegrasjonsTest {

    @Inject
    private JdbcTemplate jdbcTemplate;

    private BrukerRegistreringRepository brukerRegistreringRepository;

    @BeforeEach
    public void setup() {
        setupInMemoryDatabaseContext();
        brukerRegistreringRepository = new BrukerRegistreringRepository(jdbcTemplate);
    }

    @Test
    public void registrerBruker() {

        AktorId aktorId = new AktorId("11111");
        OrdinaerBrukerRegistrering bruker = gyldigBrukerRegistrering();

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository.lagreOrdinaerBruker(bruker, aktorId);

        assertRegistrertBruker(bruker, ordinaerBrukerRegistrering);
    }

    @Test
    public void hentBrukerregistreringForAktorId() {
        AktorId aktorId = new AktorId("11111");
        OrdinaerBrukerRegistrering bruker1 = gyldigBrukerRegistrering().setBesvarelse(gyldigBesvarelse()
                .setAndreForhold(AndreForholdSvar.JA));
        OrdinaerBrukerRegistrering bruker2 = gyldigBrukerRegistrering().setBesvarelse(gyldigBesvarelse()
                .setAndreForhold(AndreForholdSvar.NEI));

        brukerRegistreringRepository.lagreOrdinaerBruker(bruker1, aktorId);
        brukerRegistreringRepository.lagreOrdinaerBruker(bruker2, aktorId);

        OrdinaerBrukerRegistrering registrering = brukerRegistreringRepository.hentOrdinaerBrukerregistreringForAktorId(aktorId);
        assertRegistrertBruker(bruker2, registrering);
    }

    @Test
    public void hentSykmeldtregistreringForAktorId() {
        AktorId aktorId = new AktorId("11111");
        SykmeldtRegistrering bruker1 = gyldigSykmeldtRegistrering().setBesvarelse(gyldigSykmeldtSkalTilbakeSammeJobbBesvarelse()
                .setTilbakeIArbeid(TilbakeIArbeidSvar.JA_FULL_STILLING));
        SykmeldtRegistrering bruker2 = gyldigSykmeldtRegistrering().setBesvarelse(gyldigSykmeldtSkalTilbakeSammeJobbBesvarelse()
                .setTilbakeIArbeid(TilbakeIArbeidSvar.JA_REDUSERT_STILLING));

        brukerRegistreringRepository.lagreSykmeldtBruker(bruker1, aktorId);
        brukerRegistreringRepository.lagreSykmeldtBruker(bruker2, aktorId);

        SykmeldtRegistrering registrering = brukerRegistreringRepository.hentSykmeldtregistreringForAktorId(aktorId);
        assertSykmeldtRegistrertBruker(bruker2, registrering);
    }

    @Test
    public void hentOrdinaerBrukerRegistreringForAktorId(){

        AktorId aktorId = new AktorId("11111");

        OrdinaerBrukerRegistrering bruker = gyldigBrukerRegistrering().setBesvarelse(gyldigBesvarelse()
                .setAndreForhold(AndreForholdSvar.JA));

        OrdinaerBrukerRegistrering lagretBruker = brukerRegistreringRepository.lagreOrdinaerBruker(bruker, aktorId);
        bruker.setId(lagretBruker.getId()).setOpprettetDato(lagretBruker.getOpprettetDato());

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository
                .hentOrdinaerBrukerregistreringForAktorId(aktorId);

        assertEquals(bruker, ordinaerBrukerRegistrering);

    }

    @Test
    public void hentOrdinaerBrukerRegistreringForAktorIdSkalReturnereNullHvisBrukerIkkeErRegistret(){
        AktorId uregistrertAktorId = new AktorId("9876543");
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
