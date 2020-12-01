package no.nav.veilarbregistrering.integrasjonstest;

import io.vavr.control.Try;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.db.DatabaseConfig;
import no.nav.fo.veilarbregistrering.db.MigrationUtils;
import no.nav.fo.veilarbregistrering.db.profilering.ProfileringRepositoryImpl;
import no.nav.fo.veilarbregistrering.db.registrering.RegistreringTilstandRepositoryImpl;
import no.nav.fo.veilarbregistrering.db.registrering.BrukerRegistreringRepositoryImpl;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayImpl;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingStatusData;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.ProfileringService;
import no.nav.fo.veilarbregistrering.registrering.bruker.*;
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerProfilertProducer;
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertProducer;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingGateway;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykemeldingGatewayImpl;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient;
import org.junit.AfterClass;
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
import static no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder.lagProfilering;
import static no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering;
import static no.nav.veilarbregistrering.db.DatabaseTestContext.setupInMemoryDatabaseContext;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

class BrukerRegistreringServiceIntegrationTest {

    private static AnnotationConfigApplicationContext context;

    private static BrukerRegistreringService brukerRegistreringService;
    private static OppfolgingClient oppfolgingClient;
    private static BrukerRegistreringRepository brukerRegistreringRepository;
    private static ProfileringService profileringService;

    private static final Foedselsnummer ident = Foedselsnummer.of("10108000398"); //Aremark fiktivt fnr.";
    private static final Bruker BRUKER = Bruker.of(ident, AktorId.of("AKTØRID"));
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
        brukerRegistreringService = context.getBean(BrukerRegistreringService.class);
        oppfolgingClient = context.getBean(OppfolgingClient.class);
        profileringService = context.getBean(ProfileringService.class);
    }

    @AfterEach
    public void tearDown() {
        context.stop();
    }

    @AfterClass
    public static void cleanup() {
        context.close();
        context = null;
    }

    @Test
    public void skalRulleTilbakeDatabaseDersomKallTilArenaFeiler() {
        cofigureMocks();
        doThrow(new RuntimeException()).when(oppfolgingClient).aktiverBruker(any());

        Try<Void> run = Try.run(() -> brukerRegistreringService.registrerBruker(SELVGAENDE_BRUKER, BRUKER));
        assertThat(run.isFailure()).isTrue();

        Optional<OrdinaerBrukerRegistrering> brukerRegistrering = ofNullable(brukerRegistreringRepository.hentBrukerregistreringForId(1L));

        assertThat(brukerRegistrering.isPresent()).isFalse();
    }

    private void cofigureMocks() {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(new OppfolgingStatusData().withUnderOppfolging(false).withKanReaktiveres(false));
        when(profileringService.profilerBruker(anyInt(), any(), any())).thenReturn(lagProfilering());
    }

    @Configuration
    @ComponentScan
    public static class BrukerregistreringConfigTest {

        @Bean
        public BrukerRegistreringRepository brukerRegistreringRepository(JdbcTemplate db) {
            return new BrukerRegistreringRepositoryImpl(db);
        }

        @Bean
        RegistreringTilstandRepository registreringTilstandRepository(JdbcTemplate db) {
            return new RegistreringTilstandRepositoryImpl(db);
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
        public OppfolgingGateway oppfolgingGateway(OppfolgingClient oppfolgingClient) {
            return new OppfolgingGatewayImpl(oppfolgingClient);
        }

        @Bean
        public SykmeldtInfoClient sykmeldtInfoClient() {
            return mock(SykmeldtInfoClient.class);
        }

        @Bean
        public SykemeldingGateway sykemeldingGateway(SykmeldtInfoClient sykmeldtInfoClient){
            return new SykemeldingGatewayImpl(sykmeldtInfoClient);
        }

        @Bean
        public SykemeldingService sykemeldingService(SykemeldingGateway sykemeldingGateway) {
            return new SykemeldingService(sykemeldingGateway);
        }

        @Bean
        public ProfileringService profileringService() {
            return mock(ProfileringService.class);
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
                BrukerTilstandService brukerTilstandService) {

            return new BrukerRegistreringService(
                    brukerRegistreringRepository,
                    profileringRepository,
                    oppfolgingGateway,
                    profileringService,
                    registreringTilstandRepository,
                    brukerTilstandService);
        }

        @Bean
        VeilarbAbacPepClient pepClient() {
            return mock(VeilarbAbacPepClient.class);
        }

        @Bean
        ArbeidssokerRegistrertProducer meldingsSender() {
            return (event) -> {
                //noop
            };
        }

        @Bean
        ArbeidssokerProfilertProducer arbeidssokerProfilertProducer() {
            return (aktorId, innsatsgruppe, profilertDato) -> {
                //noop
            };
        }
    }


}
