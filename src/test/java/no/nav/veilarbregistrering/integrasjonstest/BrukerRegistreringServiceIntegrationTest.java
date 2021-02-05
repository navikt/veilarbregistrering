package no.nav.veilarbregistrering.integrasjonstest;

import io.vavr.control.Try;
import no.nav.common.featuretoggle.UnleashService;
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.db.DatabaseConfig;
import no.nav.fo.veilarbregistrering.db.MigrationUtils;
import no.nav.fo.veilarbregistrering.db.RepositoryConfig;
import no.nav.fo.veilarbregistrering.metrics.MetricsService;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.ProfileringService;
import no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder;
import no.nav.fo.veilarbregistrering.registrering.bruker.*;
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerException;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstand;
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstandRepository;
import no.nav.fo.veilarbregistrering.registrering.tilstand.Status;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder.aremark;
import static no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@JdbcTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
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
    private ProfileringRepository profileringRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Bruker BRUKER = Bruker.of(aremark(), AktorId.of("AKTØRID"));

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
        assertThat(run.getCause()).isInstanceOf(AktiverBrukerException.class);

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
        assertThat(run.getCause()).isInstanceOf(AktiverBrukerException.class);

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
        public UnleashService unleashService() { return mock(UnleashService.class); }

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
        public BrukerTilstandService hentBrukerTilstandService(OppfolgingGateway oppfolgingGateway, SykemeldingService sykemeldingService, UnleashService unleashService) {
            return new BrukerTilstandService(oppfolgingGateway, sykemeldingService, unleashService);
        }

        @Bean
        public MetricsService metricsService() {
            return mock(MetricsService.class);
        }

        @Bean
        BrukerRegistreringService brukerRegistreringService(
                BrukerRegistreringRepository brukerRegistreringRepository,
                ProfileringRepository profileringRepository,
                OppfolgingGateway oppfolgingGateway,
                ProfileringService profileringService,
                RegistreringTilstandRepository registreringTilstandRepository,
                BrukerTilstandService brukerTilstandService,
                ManuellRegistreringRepository manuellRegistreringRepository,
                MetricsService metricsService) {

            return new BrukerRegistreringService(
                    brukerRegistreringRepository,
                    profileringRepository,
                    oppfolgingGateway,
                    profileringService,
                    registreringTilstandRepository,
                    brukerTilstandService,
                    manuellRegistreringRepository,
                    metricsService);
        }

        @Bean
        AutorisasjonService pepClient() {
            return mock(AutorisasjonService.class);
        }

    }
}
