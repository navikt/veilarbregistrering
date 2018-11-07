package no.nav.fo.veilarbregistrering.db;

import no.nav.fo.veilarbregistrering.domain.*;
import no.nav.fo.veilarbregistrering.domain.besvarelse.AndreForholdSvar;
import no.nav.fo.veilarbregistrering.domain.besvarelse.TilbakeEtter52ukerSvar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;

import static no.nav.fo.veilarbregistrering.utils.TestUtils.*;
import static no.nav.veilarbregistrering.db.DatabaseTestContext.setupInMemoryDatabaseContext;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ArbeidssokerregistreringRepositoryIntegrationTest extends IntegrasjonsTest {

    @Inject
    private JdbcTemplate jdbcTemplate;

    private ArbeidssokerregistreringRepository arbeidssokerregistreringRepository;

    @BeforeEach
    public void setup() {
        setupInMemoryDatabaseContext();
        arbeidssokerregistreringRepository = new ArbeidssokerregistreringRepository(jdbcTemplate);
    }

    @Test
    public void profilerBruker() {
        Profilering profilering = new Profilering()
                .setAlder(39)
                .setJobbetSammenhengendeSeksAvTolvSisteManeder(true)
                .setInnsatsgruppe(Innsatsgruppe.BEHOV_FOR_ARBEIDSEVNEVURDERING);

        arbeidssokerregistreringRepository.lagreProfilering(9, profilering);
    }

    @Test
    public void registrerBruker() {

        AktorId aktorId = new AktorId("11111");
        OrdinaerBrukerRegistrering bruker = gyldigBrukerRegistrering();

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = arbeidssokerregistreringRepository.lagreOrdinaerBruker(bruker, aktorId);

        assertRegistrertBruker(bruker, ordinaerBrukerRegistrering);
    }

    @Test
    public void hentBrukerregistreringForAktorId() {
        AktorId aktorId = new AktorId("11111");
        OrdinaerBrukerRegistrering bruker1 = gyldigBrukerRegistrering().setBesvarelse(gyldigBesvarelse()
                .setAndreForhold(AndreForholdSvar.JA));
        OrdinaerBrukerRegistrering bruker2 = gyldigBrukerRegistrering().setBesvarelse(gyldigBesvarelse()
                .setAndreForhold(AndreForholdSvar.NEI));

        arbeidssokerregistreringRepository.lagreOrdinaerBruker(bruker1, aktorId);
        arbeidssokerregistreringRepository.lagreOrdinaerBruker(bruker2, aktorId);

        OrdinaerBrukerRegistrering registrering = arbeidssokerregistreringRepository.hentBrukerregistreringForAktorId(aktorId);
        assertRegistrertBruker(bruker2, registrering);
    }

    @Test
    public void hentSykmeldtregistreringForAktorId() {
        AktorId aktorId = new AktorId("11111");
        SykmeldtRegistrering bruker1 = gyldigSykmeldtRegistrering().setBesvarelse(gyldigSykmeldtBesvarelse()
                .setTilbakeEtter52uker(TilbakeEtter52ukerSvar.JA_FULL_STILLING));
        SykmeldtRegistrering bruker2 = gyldigSykmeldtRegistrering().setBesvarelse(gyldigSykmeldtBesvarelse()
                .setTilbakeEtter52uker(TilbakeEtter52ukerSvar.JA_REDUSERT_STILLING));

        arbeidssokerregistreringRepository.lagreSykmeldtBruker(bruker1, aktorId);
        arbeidssokerregistreringRepository.lagreSykmeldtBruker(bruker2, aktorId);

        SykmeldtRegistrering registrering = arbeidssokerregistreringRepository.hentSykmeldtregistreringForAktorId(aktorId);
        assertSykmeldtRegistrertBruker(bruker2, registrering);
    }

    @Test
    public void hentProfilertBrukerRegistreringForAktorId(){

        AktorId aktorId = new AktorId("11111");

        OrdinaerBrukerRegistrering bruker = gyldigBrukerRegistrering().setBesvarelse(gyldigBesvarelse()
                .setAndreForhold(AndreForholdSvar.JA));

        Profilering profilering = lagProfilering();

        OrdinaerBrukerRegistrering lagretBruker = arbeidssokerregistreringRepository.lagreOrdinaerBruker(bruker, aktorId);
        bruker.setId(lagretBruker.getId()).setOpprettetDato(lagretBruker.getOpprettetDato());
        arbeidssokerregistreringRepository.lagreProfilering(bruker.getId(), profilering);

        ProfilertBrukerRegistrering profilertBrukerRegistrering = arbeidssokerregistreringRepository.hentProfilertBrukerregistreringForAktorId(aktorId);

        assertEquals(new ProfilertBrukerRegistrering(bruker, profilering), profilertBrukerRegistrering);

    }

    @Test
    public void hentProfilertBrukerRegistreringForAktorIdSkalReturnereNullHvisBrukerIkkeErRegistret(){
        AktorId uregistrertAktorId = new AktorId("9876543");
        ProfilertBrukerRegistrering profilertBrukerRegistrering = arbeidssokerregistreringRepository.hentProfilertBrukerregistreringForAktorId(uregistrertAktorId);

        assertNull(profilertBrukerRegistrering);
    }

    private void assertRegistrertBruker(OrdinaerBrukerRegistrering bruker, OrdinaerBrukerRegistrering ordinaerBrukerRegistrering) {
        assertThat(ordinaerBrukerRegistrering.isEnigIOppsummering()).isEqualTo(bruker.isEnigIOppsummering());
        assertThat(ordinaerBrukerRegistrering.getOppsummering()).isEqualTo(bruker.getOppsummering());
        assertThat(ordinaerBrukerRegistrering.getBesvarelse()).isEqualTo(bruker.getBesvarelse());
        assertThat(ordinaerBrukerRegistrering.getSisteStilling()).isEqualTo(bruker.getSisteStilling());
        assertThat(ordinaerBrukerRegistrering.getTeksterForBesvarelse()).isEqualTo(bruker.getTeksterForBesvarelse());
    }

    private void assertSykmeldtRegistrertBruker(SykmeldtRegistrering bruker, SykmeldtRegistrering sykmeldtRegistrering) {
        assertThat(sykmeldtRegistrering.getBesvarelse()).isEqualTo(bruker.getBesvarelse());
        assertThat(sykmeldtRegistrering.getTeksterForBesvarelse()).isEqualTo(bruker.getTeksterForBesvarelse());
    }
}