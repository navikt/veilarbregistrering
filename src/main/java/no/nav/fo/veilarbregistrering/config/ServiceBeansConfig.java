package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.resources.ArbeidsforholdResource;
import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.fo.veilarbregistrering.bruker.resources.InternalIdentServlet;
import no.nav.fo.veilarbregistrering.bruker.resources.KontaktinfoResource;
import no.nav.fo.veilarbregistrering.db.oppgave.OppgaveRepositoryImpl;
import no.nav.fo.veilarbregistrering.db.profilering.ProfileringRepositoryImpl;
import no.nav.fo.veilarbregistrering.db.registrering.BrukerRegistreringRepositoryImpl;
import no.nav.fo.veilarbregistrering.db.registrering.ManuellRegistreringRepositoryImpl;
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.oppgave.*;
import no.nav.fo.veilarbregistrering.oppgave.resources.OppgaveResource;
import no.nav.fo.veilarbregistrering.orgenhet.HentEnheterGateway;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.PubliseringAvProfileringHistorikk;
import no.nav.fo.veilarbregistrering.profilering.StartRegistreringUtils;
import no.nav.fo.veilarbregistrering.registrering.bruker.*;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService;
import no.nav.fo.veilarbregistrering.registrering.resources.InternalRegistreringResendingServlet;
import no.nav.fo.veilarbregistrering.registrering.resources.InternalRegistreringTilstandServlet;
import no.nav.fo.veilarbregistrering.registrering.resources.RegistreringResource;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingGateway;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.resources.SykemeldingResource;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

@Configuration
public class ServiceBeansConfig {

    @Bean
    SykemeldingService sykemeldingService(SykemeldingGateway sykemeldingGateway) {
        return new SykemeldingService(sykemeldingGateway);
    }

    @Bean
    BrukerRegistreringService registrerBrukerService(
            BrukerRegistreringRepository brukerRegistreringRepository,
            ProfileringRepository profileringRepository,
            OppfolgingGateway oppfolgingGateway,
            PersonGateway personGateway,
            SykemeldingService sykemeldingService,
            ArbeidsforholdGateway arbeidsforholdGateway,
            ManuellRegistreringService manuellRegistreringService,
            StartRegistreringUtils startRegistreringUtils,
            UnleashService unleashService,
            ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer,
            ArbeidssokerProfilertProducer arbeidssokerProfilertProducer
    ) {
        return new BrukerRegistreringService(
                brukerRegistreringRepository,
                profileringRepository,
                oppfolgingGateway,
                personGateway,
                sykemeldingService,
                arbeidsforholdGateway,
                manuellRegistreringService,
                startRegistreringUtils,
                unleashService,
                arbeidssokerRegistrertProducer,
                arbeidssokerProfilertProducer);
    }

    @Bean
    PubliseringAvHistorikkTask publiseringAvHistorikkTask(
            BrukerRegistreringRepository brukerRegistreringRepository,
            ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer,
            UnleashService unleashService
    ) {
        return new PubliseringAvHistorikkTask(
                brukerRegistreringRepository,
                arbeidssokerRegistrertProducer,
                unleashService
        );
    }


    @Bean
    PubliseringAvProfileringHistorikk publiseringAvProfileringHistorikk(
            ProfileringRepository profileringRepository,
            BrukerRegistreringRepository brukerRegistreringRepository,
            ArbeidssokerProfilertProducer arbeidssokerProfilertProducer,
            UnleashService unleashService
    ) {
        return new PubliseringAvProfileringHistorikk(
                profileringRepository,
                brukerRegistreringRepository,
                arbeidssokerProfilertProducer,
                unleashService
        );
    }

    @Bean
    RegistreringResource registreringResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            ManuellRegistreringService manuellRegistreringService,
            BrukerRegistreringService brukerRegistreringService,
            UnleashService unleashService
    ) {
        return new RegistreringResource(
                pepClient,
                userService,
                manuellRegistreringService,
                brukerRegistreringService,
                unleashService
        );
    }

    @Bean
    ArbeidsforholdResource arbeidsforholdResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            ArbeidsforholdGateway arbeidsforholdGateway
    ) {
        return new ArbeidsforholdResource(
                pepClient,
                userService,
                arbeidsforholdGateway
        );
    }

    @Bean
    SykemeldingResource sykemeldingResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            SykemeldingService sykemeldingService
    ) {
        return new SykemeldingResource(
                pepClient,
                userService,
                sykemeldingService
        );
    }

    @Bean
    OppgaveRepository oppgaveRepository(JdbcTemplate jdbcTemplate) {
        return new OppgaveRepositoryImpl(jdbcTemplate);
    }

    @Bean
    OppgaveService oppgaveService(
            OppgaveGateway oppgaveGateway,
            OppgaveRepository oppgaveRepository,
            OppgaveRouter oppgaveRouter,
            KontaktBrukerHenvendelseProducer kontaktBrukerHenvendelseProducer) {
        return new OppgaveService(
                oppgaveGateway,
                oppgaveRepository,
                oppgaveRouter,
                kontaktBrukerHenvendelseProducer);
    }

    @Bean
    OppgaveRouter oppgaveRouter(
            ArbeidsforholdGateway arbeidsforholdGateway,
            EnhetGateway enhetGateway,
            Norg2Gateway norg2Gateway,
            PersonGateway personGateway) {
        return new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway);
    }

    @Bean
    OppgaveResource oppgaveResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            OppgaveService oppgaveService,
            OppgaveRouter oppgaveRouterProxy
    ) {
        return new OppgaveResource(pepClient, userService, oppgaveService, oppgaveRouterProxy);
    }

    @Bean
    ManuellRegistreringService manuellRegistreringService(
            ManuellRegistreringRepository manuellRegistreringRepository,
            HentEnheterGateway hentEnheterGateway) {
        return new ManuellRegistreringService(manuellRegistreringRepository, hentEnheterGateway);
    }

    @Bean
    BrukerRegistreringRepository brukerRegistreringRepository(JdbcTemplate db) {
        return new BrukerRegistreringRepositoryImpl(db);
    }

    @Bean
    ManuellRegistreringRepository manuellRegistreringRepository(JdbcTemplate db) {
        return new ManuellRegistreringRepositoryImpl(db);
    }

    @Bean
    ProfileringRepository profileringRepository(JdbcTemplate db) {
        return new ProfileringRepositoryImpl(db);
    }

    @Bean
    ArenaOverforingService arenaOverforingService(
            ProfileringRepository profileringRepository,
            BrukerRegistreringRepository brukerRegistreringRepository,
            OppfolgingGateway oppfolgingGateway,
            ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer) {
        return new ArenaOverforingService(
                profileringRepository,
                brukerRegistreringRepository,
                oppfolgingGateway,
                arbeidssokerRegistrertProducer);
    }

    @Bean
    StartRegistreringUtils startRegistreringUtils() {
        return new StartRegistreringUtils();
    }

    @Bean
    UserService userService(Provider<HttpServletRequest> provider, AktorGateway aktorGateway) {
        return new UserService(provider, aktorGateway);
    }

    @Bean
    OppholdstillatelseService datakvalitetOppholdstillatelseService(
            PdlOppslagGateway pdlOppslagGateway,
            UnleashService unleashService) {
        return new OppholdstillatelseServiceImpl(pdlOppslagGateway, unleashService);
    }

    @Bean
    KontaktinfoService kontaktinfoService(
            PdlOppslagGateway pdlOppslagGateway,
            KrrGateway krrGateway) {
        return new KontaktinfoService(pdlOppslagGateway, krrGateway);
    }

    @Bean
    KontaktinfoResource kontaktinfoResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            KontaktinfoService kontaktinfoService,
            UnleashService unleashService) {
        return new KontaktinfoResource(pepClient, userService, kontaktinfoService, unleashService);
    }

    @Bean
    InternalRegistreringTilstandServlet internalRegistreringTilstandServlet(BrukerRegistreringService brukerRegistreringService, UnleashService unleashService) {
        return new InternalRegistreringTilstandServlet(brukerRegistreringService);
    }

    @Bean
    InternalRegistreringResendingServlet internalRegistreringResendingServlet(BrukerRegistreringService brukerRegistreringService, UnleashService unleashService) {
        return new InternalRegistreringResendingServlet(brukerRegistreringService);
    }

    @Bean
    InternalIdentServlet internalIdentServlet(UserService userService) {
        return new InternalIdentServlet(userService);
    }
}
