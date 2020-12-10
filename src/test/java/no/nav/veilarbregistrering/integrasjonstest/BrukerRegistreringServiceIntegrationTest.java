package no.nav.veilarbregistrering.integrasjonstest;

import io.vavr.control.Try;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.db.DatabaseConfig;
import no.nav.fo.veilarbregistrering.db.MigrationUtils;
import no.nav.fo.veilarbregistrering.db.RepositoryConfig;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.ProfileringService;
import no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder;
import no.nav.fo.veilarbregistrering.registrering.bruker.*;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstand;
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstandRepository;
import no.nav.fo.veilarbregistrering.registrering.tilstand.Status;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.ws.rs.WebApplicationException;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder.aremark;
import static no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder.lagProfilering;
import static no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
@ContextConfiguration(classes = {DatabaseConfig.class, RepositoryConfig.class, BrukerRegistreringServiceIntegrationTest.BrukerregistreringConfigTest.class})
class BrukerRegistreringServiceIntegrationTest {

    @Autowired
    private BrukerRegistreringService brukerRegistreringService;
    @Autowired
    private OppfolgingGateway oppfolgingGateway;
    @Autowired
    private BrukerRegistreringRepository brukerRegistreringRepository;
    @Autowired
    private RegistreringTilstandRepository registreringTilstandRepository;
    @Autowired
    private ProfileringService profileringService;
    @Autowired
    private ProfileringRepository profileringRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Bruker BRUKER = Bruker.of(aremark(), AktorId.of("AKTØRID"));
    private static final OrdinaerBrukerRegistrering SELVGAENDE_BRUKER = gyldigBrukerRegistrering();

    @BeforeEach
    public void setup() {
        reset(oppfolgingGateway);
        MigrationUtils.createTables(jdbcTemplate);
    }

    @AfterEach
    public void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "BRUKER_PROFILERING");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "REGISTRERING_TILSTAND");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "BRUKER_REGISTRERING");
    }

    @Test
    public void skal_Rulle_Tilbake_Database_Dersom_Kall_Til_Arena_Feiler() {
        when(oppfolgingGateway.hentOppfolgingsstatus(any())).thenReturn(new Oppfolgingsstatus(false, false, null,null,null,null));
        when(profileringService.profilerBruker(anyInt(), any(), any())).thenReturn(lagProfilering());
        doThrow(new RuntimeException()).when(oppfolgingGateway).aktiverBruker(any(), any());

        Try<Void> run = Try.run(() -> brukerRegistreringService.registrerBruker(SELVGAENDE_BRUKER, BRUKER, null));
        assertThat(run.isFailure()).isTrue();
        assertThat(run.getCause().toString()).isEqualTo(RuntimeException.class.getName());

        Optional<OrdinaerBrukerRegistrering> brukerRegistrering = ofNullable(brukerRegistreringRepository.hentBrukerregistreringForId(1L));

        assertThat(brukerRegistrering).isEmpty();
    }

    @Test
    public void skal_Rulle_Tilbake_Database_Dersom_Overforing_Til_Arena_Feiler() {
        doThrow(new RuntimeException()).when(oppfolgingGateway).aktiverBruker(any(), any());

        long id = brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER).getId();
        registreringTilstandRepository.lagre(RegistreringTilstand.medStatus(Status.MOTTATT, id));
        profileringRepository.lagreProfilering(id, ProfileringTestdataBuilder.lagProfilering());

        Try<Void> run = Try.run(() -> brukerRegistreringService.overforArena(id, BRUKER, null));
        assertThat(run.isFailure()).isTrue();
        assertThat(run.getCause().toString()).isEqualTo(RuntimeException.class.getName());

        Optional<OrdinaerBrukerRegistrering> brukerRegistrering = ofNullable(brukerRegistreringRepository.hentBrukerregistreringForId(id));
        RegistreringTilstand registreringTilstand = registreringTilstandRepository.hentTilstandFor(id);

        assertThat(brukerRegistrering).isNotEmpty();
        assertThat(registreringTilstand.getStatus()).isEqualTo(Status.MOTTATT);
    }

    @Test
    public void skal_sette_registreringsstatus_dersom_arenafeil_er_dod_eller_utvandret() {
        when(oppfolgingGateway.aktiverBruker(any(), any())).thenReturn(AktiverBrukerResultat.Companion.feilFrom(AktiverBrukerFeil.BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET));

        long id = brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER).getId();
        registreringTilstandRepository.lagre(RegistreringTilstand.medStatus(Status.MOTTATT, id));
        profileringRepository.lagreProfilering(id, ProfileringTestdataBuilder.lagProfilering());

        Try<Void> run = Try.run(() -> brukerRegistreringService.overforArena(id, BRUKER, null));
        assertThat(run.isFailure()).isTrue();
        assertThat(run.getCause()).isInstanceOf(WebApplicationException.class);

        Optional<OrdinaerBrukerRegistrering> brukerRegistrering = ofNullable(brukerRegistreringRepository.hentBrukerregistreringForId(id));
        RegistreringTilstand registreringTilstand = registreringTilstandRepository.hentTilstandFor(id);

        assertThat(brukerRegistrering).isNotEmpty();
        assertThat(registreringTilstand.getStatus()).isEqualTo(Status.DOD_UTVANDRET_ELLER_FORSVUNNET);
    }

    @Test
    public void skal_sette_registreringsstatus_dersom_arenafeil_er_mangler_opphold() {
        AktiverBrukerResultat aktiverBrukerResultat = AktiverBrukerResultat.Companion.feilFrom(AktiverBrukerFeil.BRUKER_MANGLER_ARBEIDSTILLATELSE);
        when(oppfolgingGateway.aktiverBruker(any(), any())).thenReturn(aktiverBrukerResultat);

        long id = brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER).getId();
        registreringTilstandRepository.lagre(RegistreringTilstand.medStatus(Status.MOTTATT, id));
        profileringRepository.lagreProfilering(id, ProfileringTestdataBuilder.lagProfilering());

        Try<Void> run = Try.run(() -> brukerRegistreringService.overforArena(id, BRUKER, null));
        assertThat(run.isFailure()).isTrue();
        assertThat(run.getCause()).isInstanceOf(WebApplicationException.class);

        Optional<OrdinaerBrukerRegistrering> brukerRegistrering = ofNullable(brukerRegistreringRepository.hentBrukerregistreringForId(id));
        RegistreringTilstand registreringTilstand = registreringTilstandRepository.hentTilstandFor(id);

        assertThat(brukerRegistrering).isNotEmpty();
        assertThat(registreringTilstand.getStatus()).isEqualTo(Status.MANGLER_ARBEIDSTILLATELSE);
    }

    @Test
    public void gitt_at_overforing_til_arena_gikk_bra_skal_status_oppdateres_til_overfort_arena() {
        when(oppfolgingGateway.aktiverBruker(any(), any())).thenReturn(AktiverBrukerResultat.Companion.ok());
        long id = brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER).getId();
        registreringTilstandRepository.lagre(RegistreringTilstand.medStatus(Status.MOTTATT, id));
        profileringRepository.lagreProfilering(id, ProfileringTestdataBuilder.lagProfilering());

        Try<Void> run = Try.run(() -> brukerRegistreringService.overforArena(id, BRUKER, null));
        assertThat(run.isSuccess()).isTrue();

        Optional<OrdinaerBrukerRegistrering> brukerRegistrering = ofNullable(brukerRegistreringRepository.hentBrukerregistreringForId(id));
        RegistreringTilstand registreringTilstand = registreringTilstandRepository.hentTilstandFor(id);

        assertThat(brukerRegistrering).isNotEmpty();
        assertThat(registreringTilstand.getStatus()).isEqualTo(Status.OVERFORT_ARENA);
    }

    @Configuration
    public static class BrukerregistreringConfigTest {

        @Bean
        public OppfolgingGateway oppfolgingGateway() {
            return mock(OppfolgingGateway.class);
        }

        @Bean
        public SykemeldingService sykemeldingService() {
            return mock(SykemeldingService.class);
        }

        @Bean
        public ProfileringService profileringService() {
            return mock(ProfileringService.class);
        }

        @Bean
        public ManuellRegistreringRepository manuellRegistreringRepository() {
            return mock(ManuellRegistreringRepository.class);
        }

        @Bean
        public BrukerTilstandService hentBrukerTilstandService(OppfolgingGateway oppfolgingGateway, SykemeldingService sykemeldingService) {
            return new BrukerTilstandService(oppfolgingGateway, sykemeldingService);
        }

        @Bean
        BrukerRegistreringService brukerRegistreringService(
                BrukerRegistreringRepository brukerRegistreringRepository,
                ProfileringRepository profileringRepository,
                OppfolgingGateway oppfolgingGateway,
                ProfileringService profileringService,
                RegistreringTilstandRepository registreringTilstandRepository,
                BrukerTilstandService brukerTilstandService,
                ManuellRegistreringRepository manuellRegistreringRepository) {

            return new BrukerRegistreringService(
                    brukerRegistreringRepository,
                    profileringRepository,
                    oppfolgingGateway,
                    profileringService,
                    registreringTilstandRepository,
                    brukerTilstandService,
                    manuellRegistreringRepository);
        }

        @Bean
        VeilarbAbacPepClient pepClient() {
            return mock(VeilarbAbacPepClient.class);
        }

    }
}
