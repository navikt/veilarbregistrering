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
        BrukerRegistrering bruker = gyldigBrukerRegistrering();

        BrukerRegistrering brukerRegistrering = arbeidssokerregistreringRepository.lagreBruker(bruker, aktorId);

        assertRegistrertBruker(bruker, brukerRegistrering);
    }

    @Test
    public void hentBrukerregistreringForAktorId() {
        AktorId aktorId = new AktorId("11111");
        BrukerRegistrering bruker1 = gyldigBrukerRegistrering().setBesvarelse(gyldigBesvarelse()
                .setAndreForhold(AndreForholdSvar.JA));
        BrukerRegistrering bruker2 = gyldigBrukerRegistrering().setBesvarelse(gyldigBesvarelse()
                .setAndreForhold(AndreForholdSvar.NEI));

        arbeidssokerregistreringRepository.lagreBruker(bruker1, aktorId);
        arbeidssokerregistreringRepository.lagreBruker(bruker2, aktorId);

        BrukerRegistrering registrering = arbeidssokerregistreringRepository.hentBrukerregistreringForAktorId(aktorId);
        assertRegistrertBruker(bruker2, registrering);
    }

    @Test
    public void hentSykmeldtregistreringForAktorId() {
        AktorId aktorId = new AktorId("11111");
        BrukerRegistrering bruker1 = gyldigSykmeldtRegistrering().setBesvarelse(gyldigSykmeldtBesvarelse()
                .setTilbakeEtter52uker(TilbakeEtter52ukerSvar.JA_FULL_STILLING));
        BrukerRegistrering bruker2 = gyldigSykmeldtRegistrering().setBesvarelse(gyldigSykmeldtBesvarelse()
                .setTilbakeEtter52uker(TilbakeEtter52ukerSvar.JA_REDUSERT_STILLING));

        arbeidssokerregistreringRepository.lagreSykmeldtBruker(bruker1, aktorId);
        arbeidssokerregistreringRepository.lagreSykmeldtBruker(bruker2, aktorId);

        BrukerRegistrering registrering = arbeidssokerregistreringRepository.hentSykmeldtregistreringForAktorId(aktorId);
        assertSykmeldtRegistrertBruker(bruker2, registrering);
    }

    @Test
    public void hentProfilertBrukerRegistreringForAktorId(){

        AktorId aktorId = new AktorId("11111");

        BrukerRegistrering bruker = gyldigBrukerRegistrering().setBesvarelse(gyldigBesvarelse()
                .setAndreForhold(AndreForholdSvar.JA));

        Profilering profilering = lagProfilering();

        BrukerRegistrering lagretBruker = arbeidssokerregistreringRepository.lagreBruker(bruker, aktorId);
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

    private void assertRegistrertBruker(BrukerRegistrering bruker, BrukerRegistrering brukerRegistrering) {
        assertThat(brukerRegistrering.isEnigIOppsummering()).isEqualTo(bruker.isEnigIOppsummering());
        assertThat(brukerRegistrering.getOppsummering()).isEqualTo(bruker.getOppsummering());
        assertThat(brukerRegistrering.getBesvarelse()).isEqualTo(bruker.getBesvarelse());
        assertThat(brukerRegistrering.getSisteStilling()).isEqualTo(bruker.getSisteStilling());
        assertThat(brukerRegistrering.getTeksterForBesvarelse()).isEqualTo(bruker.getTeksterForBesvarelse());
    }

    private void assertSykmeldtRegistrertBruker(BrukerRegistrering bruker, BrukerRegistrering brukerRegistrering) {
        assertThat(brukerRegistrering.getBesvarelse()).isEqualTo(bruker.getBesvarelse());
        assertThat(brukerRegistrering.getTeksterForBesvarelse()).isEqualTo(bruker.getTeksterForBesvarelse());
    }
}