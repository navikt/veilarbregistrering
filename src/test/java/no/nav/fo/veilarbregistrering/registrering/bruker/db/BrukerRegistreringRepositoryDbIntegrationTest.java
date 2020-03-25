package no.nav.fo.veilarbregistrering.registrering.bruker.db;

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
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering;
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringTilstandTestdataBuilder.registreringTilstand;
import static no.nav.fo.veilarbregistrering.registrering.bruker.Status.ARENA_OK;
import static no.nav.veilarbregistrering.db.DatabaseTestContext.setupInMemoryDatabaseContext;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BrukerRegistreringRepositoryDbIntegrationTest extends DbIntegrasjonsTest {

    private static final Foedselsnummer FOEDSELSNUMMER = Foedselsnummer.of("12345678911");
    private static final AktorId AKTOR_ID_11111 = AktorId.valueOf("11111");
    private static final Bruker BRUKER = Bruker.of(FOEDSELSNUMMER, AKTOR_ID_11111);

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
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository.lagre(registrering, BRUKER);
        assertRegistrertBruker(registrering, ordinaerBrukerRegistrering);
    }

    @Test
    public void hentBrukerregistreringForAktorId() {

        OrdinaerBrukerRegistrering registrering1 = gyldigBrukerRegistrering().setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse()
                .setAndreForhold(AndreForholdSvar.JA));
        OrdinaerBrukerRegistrering registrering2 = gyldigBrukerRegistrering().setBesvarelse(BesvarelseTestdataBuilder.gyldigBesvarelse()
                .setAndreForhold(AndreForholdSvar.NEI));

        brukerRegistreringRepository.lagre(registrering1, BRUKER);
        brukerRegistreringRepository.lagre(registrering2, BRUKER);

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

        OrdinaerBrukerRegistrering lagretBruker = brukerRegistreringRepository.lagre(registrering, BRUKER);
        registrering.setId(lagretBruker.getId()).setOpprettetDato(lagretBruker.getOpprettetDato());

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository
                .hentOrdinaerBrukerregistreringForAktorId(AKTOR_ID_11111);

        assertEquals(registrering, ordinaerBrukerRegistrering);

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

    @Test
    public void skal_lagre_og_hente_registreringTilstand() {
        RegistreringTilstand registreringTilstand = RegistreringTilstand.ofMottattRegistrering(1235124L);

        long id = brukerRegistreringRepository.lagre(registreringTilstand);

        assertThat(id).isNotNegative();

        RegistreringTilstand lagretTilstand = brukerRegistreringRepository.hentRegistreringTilstand(id);

        assertThat(lagretTilstand.getId()).isEqualTo(id);
        assertThat(lagretTilstand.getUuid()).isNotNull();
        assertThat(lagretTilstand.getBrukerRegistreringId()).isEqualTo(1235124L);
        assertThat(lagretTilstand.getOpprettet()).isBetween(now().minusSeconds(10), now().plusSeconds(10));
        assertThat(lagretTilstand.getSistEndret()).isNull();
        assertThat(lagretTilstand.getStatus()).isEqualTo(Status.MOTTATT);
    }

    @Test
    public void skal_finne_den_eldste_tilstanden_med_status_mottatt() {
        RegistreringTilstand eldsteRegistrering = registreringTilstand()
                .brukerRegistreringId(112233L)
                .opprettet(LocalDateTime.now().minusMinutes(5))
                .build();
        long id1 = brukerRegistreringRepository.lagre(eldsteRegistrering);

        RegistreringTilstand nyesteRegistering = registreringTilstand()
                .brukerRegistreringId(332211)
                .opprettet(LocalDateTime.now().minusMinutes(1))
                .build();
        brukerRegistreringRepository.lagre(nyesteRegistering);

        RegistreringTilstand midtersteRegistering = registreringTilstand()
                .brukerRegistreringId(666666)
                .opprettet(LocalDateTime.now().minusMinutes(3))
                .build();
        brukerRegistreringRepository.lagre(midtersteRegistering);

        Optional<RegistreringTilstand> lagretTilstand = brukerRegistreringRepository.finnNesteRegistreringForOverforing();

        assertThat(lagretTilstand.get().getId()).isEqualTo(id1);
        assertThat(lagretTilstand.get().getBrukerRegistreringId()).isEqualTo(112233L);
        assertThat(lagretTilstand.get().getStatus()).isEqualTo(Status.MOTTATT);
    }

    @Test
    public void skal_returnere_empty_naar_ingen_flere_mottatte() {
        RegistreringTilstand eldsteRegistrering = registreringTilstand()
                .brukerRegistreringId(112233L)
                .opprettet(LocalDateTime.now().minusMinutes(5))
                .status(ARENA_OK)
                .build();
        brukerRegistreringRepository.lagre(eldsteRegistrering);

        RegistreringTilstand nyesteRegistering = registreringTilstand()
                .brukerRegistreringId(332211)
                .opprettet(LocalDateTime.now().minusMinutes(1))
                .status(ARENA_OK)
                .build();
        brukerRegistreringRepository.lagre(nyesteRegistering);

        RegistreringTilstand midtersteRegistering = registreringTilstand()
                .brukerRegistreringId(666666)
                .opprettet(LocalDateTime.now().minusMinutes(3))
                .status(ARENA_OK)
                .build();
        brukerRegistreringRepository.lagre(midtersteRegistering);

        Optional<RegistreringTilstand> lagretTilstand = brukerRegistreringRepository.finnNesteRegistreringForOverforing();

        assertThat(lagretTilstand.isPresent()).isFalse();
    }
}
