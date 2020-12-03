package no.nav.veilarbregistrering.integrasjonstest;

import io.vavr.control.Try;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.db.DatabaseConfig;
import no.nav.fo.veilarbregistrering.db.MigrationUtils;
import no.nav.fo.veilarbregistrering.db.RepositoryConfig;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.ProfileringService;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringService;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerTilstandService;
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering;
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstand;
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstandRepository;
import no.nav.fo.veilarbregistrering.registrering.tilstand.Status;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder.lagProfilering;
import static no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitConfig
@Transactional(transactionManager = "txMgrTest")
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
    private JdbcTemplate jdbcTemplate;

    private static final Foedselsnummer ident = Foedselsnummer.of("10108000398"); //Aremark fiktivt fnr.";
    private static final Bruker BRUKER = Bruker.of(ident, AktorId.of("AKTØRID"));
    private static final OrdinaerBrukerRegistrering SELVGAENDE_BRUKER = gyldigBrukerRegistrering();

    @BeforeAll
    public void setup() {
        MigrationUtils.createTables(jdbcTemplate);
    }

    @Test
    public void skalRulleTilbakeDatabaseDersomKallTilArenaFeiler() {
        cofigureMocks();
        doThrow(new RuntimeException()).when(oppfolgingGateway).aktiverBruker(any(), any());

        Try<Void> run = Try.run(() -> brukerRegistreringService.registrerBruker(SELVGAENDE_BRUKER, BRUKER));
        assertThat(run.isFailure()).isTrue();

        Optional<OrdinaerBrukerRegistrering> brukerRegistrering = ofNullable(brukerRegistreringRepository.hentBrukerregistreringForId(1L));

        assertThat(brukerRegistrering.isPresent()).isFalse();
    }

    @Test
    public void skalRulleTilbakeDatabaseDersomOverforingTilArenaFeiler() {
        cofigureMocks();
        doThrow(new RuntimeException()).when(oppfolgingGateway).aktiverBruker(any(), any());

        long id = brukerRegistreringRepository.lagre(gyldigBrukerRegistrering(), BRUKER).getId();
        registreringTilstandRepository.lagre(RegistreringTilstand.medStatus(Status.MOTTATT, id));

        Try<Void> run = Try.run(() -> brukerRegistreringService.overforArena(id, BRUKER));
        assertThat(run.isFailure()).isTrue();

        Optional<OrdinaerBrukerRegistrering> brukerRegistrering = ofNullable(brukerRegistreringRepository.hentBrukerregistreringForId(id));
        Optional<RegistreringTilstand> registreringTilstand = registreringTilstandRepository.hentTilstandFor(id);

        assertThat(brukerRegistrering.isPresent()).isTrue();
        assertThat(registreringTilstand.get().getStatus()).isEqualTo(Status.MOTTATT);
    }

    private void cofigureMocks() {
        when(oppfolgingGateway.hentOppfolgingsstatus(any())).thenReturn(new Oppfolgingsstatus(false, false, null,null,null,null));
        when(profileringService.profilerBruker(anyInt(), any(), any())).thenReturn(lagProfilering());
    }

    @Configuration
    @ComponentScan
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

    }
}
