package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.resources.ArbeidsforholdResource;
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerRepository;
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerService;
import no.nav.fo.veilarbregistrering.arbeidssoker.FormidlingsgruppeGateway;
import no.nav.fo.veilarbregistrering.arbeidssoker.resources.ArbeidssokerResource;
import no.nav.fo.veilarbregistrering.arbeidssoker.resources.InternalArbeidssokerServlet;
import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.fo.veilarbregistrering.bruker.resources.InternalIdentServlet;
import no.nav.fo.veilarbregistrering.bruker.resources.KontaktinfoResource;
import no.nav.fo.veilarbregistrering.db.arbeidssoker.ArbeidssokerRepositoryImpl;
import no.nav.fo.veilarbregistrering.db.oppgave.OppgaveRepositoryImpl;
import no.nav.fo.veilarbregistrering.db.profilering.ProfileringRepositoryImpl;
import no.nav.fo.veilarbregistrering.db.registrering.AktiveringTilstandRepositoryImpl;
import no.nav.fo.veilarbregistrering.db.registrering.BrukerRegistreringRepositoryImpl;
import no.nav.fo.veilarbregistrering.db.registrering.ManuellRegistreringRepositoryImpl;
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.oppgave.*;
import no.nav.fo.veilarbregistrering.oppgave.resources.OppgaveResource;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.ProfileringService;
import no.nav.fo.veilarbregistrering.registrering.bruker.*;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService;
import no.nav.fo.veilarbregistrering.registrering.resources.InternalRegistreringStatusServlet;
import no.nav.fo.veilarbregistrering.registrering.resources.InternalRegistreringStatusoversiktServlet;
import no.nav.fo.veilarbregistrering.registrering.resources.RegistreringResource;
import no.nav.fo.veilarbregistrering.registrering.scheduler.PubliseringAvHistorikkTask;
import no.nav.fo.veilarbregistrering.registrering.scheduler.PubliseringAvRegistreringEventsScheduler;
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
    HentRegistreringService hentRegistreringService(
            BrukerRegistreringRepository brukerRegistreringRepository,
            ProfileringRepository profileringRepository,
            ManuellRegistreringService manuellRegistreringService) {
        return new HentRegistreringService(
                brukerRegistreringRepository,
                profileringRepository,
                manuellRegistreringService);
    }

    @Bean
    AktiveringTilstandService aktiveringTilstandService(AktiveringTilstandRepository aktiveringTilstandRepository) {
        return new AktiveringTilstandService(aktiveringTilstandRepository);
    }

    @Bean
    BrukerTilstandService brukerTilstandService(
            OppfolgingGateway oppfolgingGateway,
            SykemeldingService sykemeldingService) {
        return new BrukerTilstandService(oppfolgingGateway, sykemeldingService);
    }

    @Bean
    StartRegistreringStatusService startRegistreringStatusService(
            ArbeidsforholdGateway arbeidsforholdGateway,
            BrukerTilstandService brukerTilstandService,
            PersonGateway personGateway) {
        return new StartRegistreringStatusService(
                arbeidsforholdGateway,
                brukerTilstandService,
                personGateway);
    }

    @Bean
    InaktivBrukerService inaktivBrukerService(
            BrukerTilstandService brukerTilstandService,
            BrukerRegistreringRepository brukerRegistreringRepository,
            OppfolgingGateway oppfolgingGateway) {
        return new InaktivBrukerService(
                brukerTilstandService,
                brukerRegistreringRepository,
                oppfolgingGateway);
    }

    @Bean
    SykmeldtRegistreringService sykmeldtRegistreringService(
            BrukerTilstandService arbeidssokerService,
            OppfolgingGateway oppfolgingGateway,
            BrukerRegistreringRepository brukerRegistreringRepository) {
        return new SykmeldtRegistreringService(
                arbeidssokerService,
                oppfolgingGateway,
                brukerRegistreringRepository);
    }

    @Bean
    BrukerRegistreringService registrerBrukerService(
            BrukerRegistreringRepository brukerRegistreringRepository,
            ProfileringRepository profileringRepository,
            OppfolgingGateway oppfolgingGateway,
            ProfileringService profileringService,
            AktiveringTilstandRepository aktiveringTilstandRepository,
            BrukerTilstandService brukerTilstandService) {
        return new BrukerRegistreringService(
                brukerRegistreringRepository,
                profileringRepository,
                oppfolgingGateway,
                profileringService,
                aktiveringTilstandRepository,
                brukerTilstandService);
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
    RegistreringResource registreringResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            ManuellRegistreringService manuellRegistreringService,
            BrukerRegistreringService brukerRegistreringService,
            HentRegistreringService hentRegistreringService,
            UnleashService unleashService,
            StartRegistreringStatusService startRegistreringStatusService,
            SykmeldtRegistreringService sykmeldtRegistreringService,
            InaktivBrukerService inaktivBrukerService) {
        return new RegistreringResource(
                pepClient,
                userService,
                manuellRegistreringService,
                brukerRegistreringService,
                hentRegistreringService,
                unleashService,
                sykmeldtRegistreringService,
                startRegistreringStatusService,
                inaktivBrukerService);
    }

    @Bean
    ArbeidsforholdResource arbeidsforholdResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            ArbeidsforholdGateway arbeidsforholdGateway) {
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
            SykemeldingService sykemeldingService) {
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
                kontaktBrukerHenvendelseProducer
        );
    }

    @Bean
    OppgaveRouter oppgaveRouter(
            ArbeidsforholdGateway arbeidsforholdGateway,
            EnhetGateway enhetGateway,
            Norg2Gateway norg2Gateway,
            PersonGateway personGateway,
            UnleashService unleashService,
            PdlOppslagGateway pdlOppslagGateway) {
        return new OppgaveRouter(arbeidsforholdGateway, enhetGateway, norg2Gateway, personGateway, unleashService, pdlOppslagGateway);
    }

    @Bean
    OppgaveResource oppgaveResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            OppgaveService oppgaveService) {
        return new OppgaveResource(pepClient, userService, oppgaveService);
    }

    @Bean
    ManuellRegistreringService manuellRegistreringService(
            ManuellRegistreringRepository manuellRegistreringRepository,
            Norg2Gateway norg2Gateway) {
        return new ManuellRegistreringService(
                manuellRegistreringRepository,
                norg2Gateway);
    }

    @Bean
    BrukerRegistreringRepository brukerRegistreringRepository(JdbcTemplate db) {
        return new BrukerRegistreringRepositoryImpl(db);
    }

    @Bean
    AktiveringTilstandRepository aktiveringTilstandRepository(JdbcTemplate db) {
        return new AktiveringTilstandRepositoryImpl(db);
    }

    @Bean
    ArbeidssokerRepository arbeidssokerRepository(JdbcTemplate db) {
        return new ArbeidssokerRepositoryImpl(db);
    }

    @Bean
    ArbeidssokerService arbeidssokerService(
            ArbeidssokerRepository arbeidssokerRepository,
            FormidlingsgruppeGateway formidlingsgruppeGateway,
            UnleashService unleashService) {
        return new ArbeidssokerService(arbeidssokerRepository, formidlingsgruppeGateway, unleashService);
    }

    @Bean
    ArbeidssokerResource arbeidssokerResource(
            ArbeidssokerService arbeidssokerService,
            UserService userService,
            VeilarbAbacPepClient pepClient) {
        return new ArbeidssokerResource(arbeidssokerService, userService, pepClient);
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
    PubliseringAvEventsService publiseringAvEventsService(
            ProfileringRepository profileringRepository,
            BrukerRegistreringRepository brukerRegistreringRepository,
            ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer,
            AktiveringTilstandRepository aktiveringTilstandRepository,
            ArbeidssokerProfilertProducer arbeidssokerProfilertProducer) {
        return new PubliseringAvEventsService(
                profileringRepository,
                brukerRegistreringRepository,
                arbeidssokerRegistrertProducer,
                aktiveringTilstandRepository,
                arbeidssokerProfilertProducer
        );
    }

    @Bean
    public PubliseringAvRegistreringEventsScheduler publiseringAvRegistreringEventsScheduler(
            PubliseringAvEventsService publiseringAvEventsService
    ) {
        return new PubliseringAvRegistreringEventsScheduler(publiseringAvEventsService);
    }

    @Bean
    OppgaveForAvvistRegistreringService oppgaveForAvvistRegistreringService(
            OppgaveService oppgaveService,
            BrukerRegistreringRepository brukerRegistreringRepository,
            AktiveringTilstandRepository aktiveringTilstandRepository) {
        return new OppgaveForAvvistRegistreringService(
                oppgaveService,
                brukerRegistreringRepository,
                aktiveringTilstandRepository
        );
    }

    @Bean
    ProfileringService profileringService(ArbeidsforholdGateway arbeidsforholdGateway) {
        return new ProfileringService(arbeidsforholdGateway);
    }

    @Bean
    UserService userService(
            Provider<HttpServletRequest> provider,
            PdlOppslagGateway pdlOppslagGateway
    ) {
        return new UserService(provider, pdlOppslagGateway);
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
            KontaktinfoService kontaktinfoService) {
        return new KontaktinfoResource(pepClient, userService, kontaktinfoService);
    }

    @Bean
    InternalRegistreringStatusoversiktServlet internalRegistreringTilstandServlet(AktiveringTilstandService aktiveringTilstandService) {
        return new InternalRegistreringStatusoversiktServlet(aktiveringTilstandService);
    }

    @Bean
    InternalRegistreringStatusServlet internalRegistreringResendingServlet(AktiveringTilstandService aktiveringTilstandService) {
        return new InternalRegistreringStatusServlet(aktiveringTilstandService);
    }

    @Bean
    InternalIdentServlet internalIdentServlet(UserService userService) {
        return new InternalIdentServlet(userService);
    }

    @Bean
    InternalArbeidssokerServlet internalArbeidssokerServlet(UserService userService, ArbeidssokerService arbeidssokerService) {
        return new InternalArbeidssokerServlet(userService, arbeidssokerService);
    }
}
