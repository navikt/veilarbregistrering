package no.nav.veilarbregistrering.integrasjonstest;

import io.vavr.control.Try;
import no.nav.apiapp.security.veilarbabac.Bruker;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.config.DatabaseConfig;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.db.MigrationUtils;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingStatusData;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayImpl;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.db.ProfileringRepositoryImpl;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringService;
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering;
import no.nav.fo.veilarbregistrering.profilering.StartRegistreringUtils;
import no.nav.fo.veilarbregistrering.registrering.bruker.db.BrukerRegistreringRepositoryImpl;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykemeldingGatewayImpl;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering;
import static no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder.lagProfilering;
import static no.nav.veilarbregistrering.db.DatabaseTestContext.setupInMemoryDatabaseContext;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BrukerRegistreringServiceIntegrationTest {

    private static RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature;
    private static AnnotationConfigApplicationContext context;

    private static BrukerRegistreringService brukerRegistreringService;
    private static OppfolgingClient oppfolgingClient;
    private static BrukerRegistreringRepository brukerRegistreringRepository;
    private static ProfileringRepository profileringRepository;
    private static StartRegistreringUtils startRegistreringUtils;

    private static String ident = "10108000398"; //Aremark fiktivt fnr.";
    private static final OrdinaerBrukerRegistrering SELVGAENDE_BRUKER = gyldigBrukerRegistrering();

    @BeforeEach
    public void setup() {

        setupInMemoryDatabaseContext();

        context = new AnnotationConfigApplicationContext(
                DatabaseConfig.class,
                BrukerregistreringConfigTest.class
        );

        context.start();

        MigrationUtils.createTables((JdbcTemplate) context.getBean("jdbcTemplate"));
        brukerRegistreringRepository = context.getBean(BrukerRegistreringRepositoryImpl.class);
        profileringRepository = context.getBean(ProfileringRepositoryImpl.class);
        brukerRegistreringService = context.getBean(BrukerRegistreringService.class);
        oppfolgingClient = context.getBean(OppfolgingClient.class);
        startRegistreringUtils = context.getBean(StartRegistreringUtils.class);
        sykemeldtRegistreringFeature = context.getBean(RemoteFeatureConfig.SykemeldtRegistreringFeature.class);
    }

    @AfterEach
    public void tearDown() {
        context.stop();
    }

    @Test
    public void skalRulleTilbakeDatabaseDersomKallTilArenaFeiler() {
        cofigureMocks();
        doThrow(new RuntimeException()).when(oppfolgingClient).aktiverBruker(any());

        Try<Void> run = Try.run(() -> brukerRegistreringService.registrerBruker(SELVGAENDE_BRUKER, Bruker.fraFnr(ident).medAktoerId("AKTØRID")));
        assertThat(run.isFailure()).isTrue();

        Optional<OrdinaerBrukerRegistrering> brukerRegistrering = ofNullable(brukerRegistreringRepository.hentBrukerregistreringForId(1l));

        assertThat(brukerRegistrering.isPresent()).isFalse();
    }

    @Test
    public void skalLagreIDatabaseDersomKallTilArenaErOK() {
        cofigureMocks();

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringService.registrerBruker(SELVGAENDE_BRUKER, Bruker.fraFnr(ident).medAktoerId("AKTØRID"));

        Optional<OrdinaerBrukerRegistrering> reg = ofNullable(brukerRegistreringRepository.hentBrukerregistreringForId(ordinaerBrukerRegistrering.getId()));

        assertThat(reg.isPresent()).isTrue();
    }

    private void cofigureMocks() {
        when(sykemeldtRegistreringFeature.erSykemeldtRegistreringAktiv()).thenReturn(true);
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(new OppfolgingStatusData().withUnderOppfolging(false).withKanReaktiveres(false));
        when(startRegistreringUtils.harJobbetSammenhengendeSeksAvTolvSisteManeder(any(), any())).thenReturn(true);
        when(startRegistreringUtils.profilerBruker(anyInt(), any(), any(), any())).thenReturn(lagProfilering());
    }


    @Configuration
    @ComponentScan
    public static class BrukerregistreringConfigTest {

        @Bean
        public RemoteFeatureConfig.SykemeldtRegistreringFeature registreringFeature() {
            return mock(RemoteFeatureConfig.SykemeldtRegistreringFeature.class);
        }

        @Bean
        public BrukerRegistreringRepository brukerRegistreringRepository(JdbcTemplate db) {
            return new BrukerRegistreringRepositoryImpl(db);
        }

        @Bean
        public ProfileringRepository profileringRepository(JdbcTemplate db) {
            return new ProfileringRepositoryImpl(db);
        }

        @Bean
        public OppfolgingClient oppfolgingClient() {
            return mock(OppfolgingClient.class);
        }

        @Bean
        public SykmeldtInfoClient sykeforloepMetadataClient() {
            return mock(SykmeldtInfoClient.class);
        }

        @Bean
        public ArbeidsforholdGateway arbeidsforholdService() {
            return mock(ArbeidsforholdGateway.class);
        }

        @Bean
        public ManuellRegistreringService manuellRegistreringService() {
            return mock(ManuellRegistreringService.class);
        }

        @Bean
        public StartRegistreringUtils startRegistreringUtils() {
            return mock(StartRegistreringUtils.class);
        }


        @Bean
        BrukerRegistreringService brukerRegistreringService(
                BrukerRegistreringRepository brukerRegistreringRepository,
                ProfileringRepository profileringRepository,
                OppfolgingClient oppfolgingClient,
                SykmeldtInfoClient sykeforloepMetadataClient,
                ArbeidsforholdGateway arbeidsforholdGateway,
                ManuellRegistreringService manuellRegistreringService,
                StartRegistreringUtils startRegistreringUtils,
                RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature) {

            return new BrukerRegistreringService(
                    brukerRegistreringRepository,
                    profileringRepository,
                    new OppfolgingGatewayImpl(oppfolgingClient),
                    new SykemeldingService(new SykemeldingGatewayImpl(sykeforloepMetadataClient)),
                    arbeidsforholdGateway,
                    manuellRegistreringService,
                    startRegistreringUtils,
                    sykemeldtRegistreringFeature
            );
        }

        @Bean
        VeilarbAbacPepClient pepClient() {
            return mock(VeilarbAbacPepClient.class);
        }
    }


}
