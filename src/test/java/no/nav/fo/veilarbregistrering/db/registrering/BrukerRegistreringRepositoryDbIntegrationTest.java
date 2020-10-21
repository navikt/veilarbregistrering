package no.nav.fo.veilarbregistrering.db.registrering;

import no.nav.fo.veilarbregistrering.besvarelse.AndreForholdSvar;
import no.nav.fo.veilarbregistrering.besvarelse.BesvarelseTestdataBuilder;
import no.nav.fo.veilarbregistrering.besvarelse.TilbakeIArbeidSvar;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.db.DbIntegrasjonsTest;
import no.nav.fo.veilarbregistrering.registrering.bruker.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;

import static no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering;
import static no.nav.veilarbregistrering.db.DatabaseTestContext.setupInMemoryDatabaseContext;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BrukerRegistreringRepositoryDbIntegrationTest extends DbIntegrasjonsTest {

    private static final Foedselsnummer FOEDSELSNUMMER = Foedselsnummer.of("12345678911");
    private static final AktorId AKTOR_ID_11111 = AktorId.of("11111");
    private static final Bruker BRUKER_1 = Bruker.of(FOEDSELSNUMMER, AKTOR_ID_11111);
    private static final Foedselsnummer FOEDSELSNUMMER_2 = Foedselsnummer.of("22345678911");
    private static final AktorId AKTOR_ID_22222 = AktorId.of("22222");
    private static final Bruker BRUKER_2 = Bruker.of(FOEDSELSNUMMER_2, AKTOR_ID_22222);
    private static final Foedselsnummer FOEDSELSNUMMER_3 = Foedselsnummer.of("32345678911");
    private static final AktorId AKTOR_ID_33333 = AktorId.of("33333");
    private static final Bruker BRUKER_3 = Bruker.of(FOEDSELSNUMMER_3, AKTOR_ID_33333);

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
        OrdinaerBrukerRegistrering registrering = gyldigBrukerRegistrering();
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository.lagre(registrering, BRUKER_1);
        assertRegistrertBruker(registrering, ordinaerBrukerRegistrering);
    }

    @Test
    public void hentBrukerregistreringForAktorId() {

        OrdinaerBrukerRegistrering registrering1 = gyldigBrukerRegistrering().setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse()
                .setAndreForhold(AndreForholdSvar.JA));
        OrdinaerBrukerRegistrering registrering2 = gyldigBrukerRegistrering().setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse()
                .setAndreForhold(AndreForholdSvar.NEI));

        brukerRegistreringRepository.lagre(registrering1, BRUKER_1);
        brukerRegistreringRepository.lagre(registrering2, BRUKER_1);

        OrdinaerBrukerRegistrering registrering = brukerRegistreringRepository.hentOrdinaerBrukerregistreringForAktorId(AKTOR_ID_11111);
        assertRegistrertBruker(registrering2, registrering);
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
        OrdinaerBrukerRegistrering registrering = gyldigBrukerRegistrering().setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse()
                .setAndreForhold(AndreForholdSvar.JA));

        OrdinaerBrukerRegistrering lagretBruker = brukerRegistreringRepository.lagre(registrering, BRUKER_1);
        registrering.setId(lagretBruker.getId()).setOpprettetDato(lagretBruker.getOpprettetDato());

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository
                .hentOrdinaerBrukerregistreringForAktorId(AKTOR_ID_11111);

        assertEquals(registrering, ordinaerBrukerRegistrering);

    }

    @Test
    public void hentOrdinaerBrukerRegistreringForAktorIdSkalReturnereNullHvisBrukerIkkeErRegistret(){
        AktorId uregistrertAktorId = AktorId.of("9876543");
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

    @Test
    public void skal_hente_foedselsnummer_tilknyttet_ordinaerBrukerRegistrering() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER_1);

        Bruker bruker = brukerRegistreringRepository.hentBrukerTilknyttet(ordinaerBrukerRegistrering.getId());
        assertThat(bruker.getGjeldendeFoedselsnummer()).isEqualTo(BRUKER_1.getGjeldendeFoedselsnummer());
        assertThat(bruker.getAktorId()).isEqualTo(BRUKER_1.getAktorId());
    }

    @Test
    public void findRegistreringByPage_skal_returnere_eldste_registrering_pa_bakgrunn_av_id() {
        brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER_1);
        brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER_2);
        brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER_3);

        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<ArbeidssokerRegistrertInternalEvent> registreringByPage = brukerRegistreringRepository.findRegistreringByPage(pageRequest);

        assertThat(registreringByPage.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void findRegistreringByPage_skal_paging_for_a_levere_batcher_med_rader() {
        brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER_1);
        brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER_2);
        brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER_3);

        PageRequest pageRequest = PageRequest.of(1, 2);
        Page<ArbeidssokerRegistrertInternalEvent> registreringByPage = brukerRegistreringRepository.findRegistreringByPage(pageRequest);

        assertThat(registreringByPage.getTotalPages()).isEqualTo(2);
    }
}
