package no.nav.fo.veilarbregistrering.config;

import no.nav.common.abac.Pep;
import no.nav.common.auth.context.AuthContextHolder;
import no.nav.common.auth.context.AuthContextHolderThreadLocal;
import no.nav.common.featuretoggle.UnleashClient;
import no.nav.common.health.selftest.SelfTestChecks;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.resources.ArbeidsforholdResource;
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerRepository;
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerService;
import no.nav.fo.veilarbregistrering.arbeidssoker.FormidlingsgruppeGateway;
import no.nav.fo.veilarbregistrering.arbeidssoker.resources.ArbeidssokerResource;
import no.nav.fo.veilarbregistrering.arbeidssoker.resources.InternalArbeidssokerServlet;
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService;
import no.nav.fo.veilarbregistrering.bruker.KontaktinfoService;
import no.nav.fo.veilarbregistrering.bruker.KrrGateway;
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.bruker.resources.InternalIdentServlet;
import no.nav.fo.veilarbregistrering.bruker.resources.KontaktinfoResource;
import no.nav.fo.veilarbregistrering.db.migrering_postgres.MigreringPostgressResource;
import no.nav.fo.veilarbregistrering.db.migrering_postgres.MigreringRepositoryImpl;
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway;
import no.nav.fo.veilarbregistrering.feil.FeilHandtering;
import no.nav.fo.veilarbregistrering.helsesjekk.resources.HelsesjekkResource;
import no.nav.fo.veilarbregistrering.metrics.InfluxMetricsService;
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveRepository;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveRouter;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveService;
import no.nav.fo.veilarbregistrering.oppgave.resources.OppgaveResource;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.ProfileringService;
import no.nav.fo.veilarbregistrering.registrering.bruker.*;
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.RegistreringResource;
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository;
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandService;
import no.nav.fo.veilarbregistrering.registrering.formidling.resources.InternalRegistreringStatusServlet;
import no.nav.fo.veilarbregistrering.registrering.formidling.resources.InternalRegistreringStatusoversiktServlet;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerProfilertProducer;
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertProducer;
import no.nav.fo.veilarbregistrering.registrering.publisering.PubliseringAvEventsService;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingGateway;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.resources.SykemeldingResource;
import no.nav.fo.veilarbregistrering.tidslinje.TidslinjeAggregator;
import no.nav.fo.veilarbregistrering.tidslinje.resources.TidslinjeResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceBeansConfig {

    @Bean
    public AuthContextHolder authContextHolder() {
        return AuthContextHolderThreadLocal.instance();
    }

    @Bean
    SykemeldingService sykemeldingService(
            SykemeldingGateway sykemeldingGateway,
            AutorisasjonService autorisasjonService) {
        return new SykemeldingService(sykemeldingGateway, autorisasjonService);
    }

    @Bean
    HentRegistreringService hentRegistreringService(
            BrukerRegistreringRepository brukerRegistreringRepository,
            SykmeldtRegistreringRepository sykmeldtRegistreringRepository,
            ProfileringRepository profileringRepository,
            ManuellRegistreringRepository manuellRegistreringRepository,
            Norg2Gateway norg2Gateway) {
        return new HentRegistreringService(
                brukerRegistreringRepository,
                sykmeldtRegistreringRepository,
                profileringRepository,
                manuellRegistreringRepository,
                norg2Gateway);
    }

    @Bean
    RegistreringTilstandService registreringTilstandService(RegistreringTilstandRepository registreringTilstandRepository) {
        return new RegistreringTilstandService(registreringTilstandRepository);
    }

    @Bean
    BrukerTilstandService brukerTilstandService(
            OppfolgingGateway oppfolgingGateway,
            BrukerRegistreringRepository brukerRegistreringRepository) {
        return new BrukerTilstandService(oppfolgingGateway, brukerRegistreringRepository);
    }

    @Bean
    StartRegistreringStatusService startRegistreringStatusService(
            ArbeidsforholdGateway arbeidsforholdGateway,
            BrukerTilstandService brukerTilstandService,
            PdlOppslagGateway pdlOppslagGateway,
            InfluxMetricsService influxMetricsService) {
        return new StartRegistreringStatusService(
                arbeidsforholdGateway,
                brukerTilstandService,
                pdlOppslagGateway,
                influxMetricsService);
    }

    @Bean
    InaktivBrukerService inaktivBrukerService(
            BrukerTilstandService brukerTilstandService,
            ReaktiveringRepository reaktiveringRepository,
            OppfolgingGateway oppfolgingGateway,
            InfluxMetricsService influxMetricsService) {
        return new InaktivBrukerService(
                brukerTilstandService,
                reaktiveringRepository,
                oppfolgingGateway,
                influxMetricsService);
    }

    @Bean
    SykmeldtRegistreringService sykmeldtRegistreringService(
            BrukerTilstandService brukerTilstandService,
            OppfolgingGateway oppfolgingGateway,
            SykmeldtRegistreringRepository sykmeldtRegistreringRepository,
            ManuellRegistreringRepository manuellRegistreringRepository,
            InfluxMetricsService influxMetricsService) {
        return new SykmeldtRegistreringService(
                brukerTilstandService,
                oppfolgingGateway,
                sykmeldtRegistreringRepository,
                manuellRegistreringRepository,
                influxMetricsService);
    }

    @Bean
    BrukerRegistreringService registrerBrukerService(
            BrukerRegistreringRepository brukerRegistreringRepository,
            ProfileringRepository profileringRepository,
            OppfolgingGateway oppfolgingGateway,
            ProfileringService profileringService,
            RegistreringTilstandRepository registreringTilstandRepository,
            BrukerTilstandService brukerTilstandService,
            ManuellRegistreringRepository manuellRegistreringRepository,
            InfluxMetricsService influxMetricsService) {
        return new BrukerRegistreringService(
                brukerRegistreringRepository,
                profileringRepository,
                oppfolgingGateway,
                profileringService,
                registreringTilstandRepository,
                brukerTilstandService,
                manuellRegistreringRepository,
                influxMetricsService);
    }

    @Bean
    RegistreringResource registreringResource(
            AutorisasjonService autorisasjonService,
            UserService userService,
            BrukerRegistreringService brukerRegistreringService,
            HentRegistreringService hentRegistreringService,
            UnleashClient unleashClient,
            StartRegistreringStatusService startRegistreringStatusService,
            SykmeldtRegistreringService sykmeldtRegistreringService,
            InaktivBrukerService inaktivBrukerService) {
        return new RegistreringResource(
                autorisasjonService,
                userService,
                brukerRegistreringService,
                hentRegistreringService,
                unleashClient,
                sykmeldtRegistreringService,
                startRegistreringStatusService,
                inaktivBrukerService
        );
    }

    @Bean
    HelsesjekkResource helsesjekkResource(SelfTestChecks selfTestChecks) {
        return new HelsesjekkResource(selfTestChecks);
    }

    @Bean
    ArbeidsforholdResource arbeidsforholdResource(
            AutorisasjonService autorisasjonService,
            UserService userService,
            ArbeidsforholdGateway arbeidsforholdGateway) {
        return new ArbeidsforholdResource(
                autorisasjonService,
                userService,
                arbeidsforholdGateway);
    }

    @Bean
    SykemeldingResource sykemeldingResource(
            UserService userService,
            SykemeldingService sykemeldingService,
            AutorisasjonService autorisasjonsService) {
        return new SykemeldingResource(
                userService,
                sykemeldingService,
                autorisasjonsService);
    }

    @Bean
    OppgaveService oppgaveService(
            OppgaveGateway oppgaveGateway,
            OppgaveRepository oppgaveRepository,
            OppgaveRouter oppgaveRouter,
            InfluxMetricsService influxMetricsService) {
        return new OppgaveService(
                oppgaveGateway,
                oppgaveRepository,
                oppgaveRouter,
                influxMetricsService);
    }

    @Bean
    OppgaveRouter oppgaveRouter(
            ArbeidsforholdGateway arbeidsforholdGateway,
            EnhetGateway enhetGateway,
            Norg2Gateway norg2Gateway,
            PdlOppslagGateway pdlOppslagGateway,
            InfluxMetricsService influxMetricsService) {
        return new OppgaveRouter(
                arbeidsforholdGateway,
                enhetGateway,
                norg2Gateway,
                pdlOppslagGateway,
                influxMetricsService);
    }

    @Bean
    OppgaveResource oppgaveResource(
            UserService userService,
            OppgaveService oppgaveService,
            AutorisasjonService autorisasjonService) {
        return new OppgaveResource(userService, oppgaveService, autorisasjonService);
    }

    @Bean
    ArbeidssokerService arbeidssokerService(
            ArbeidssokerRepository arbeidssokerRepository,
            FormidlingsgruppeGateway formidlingsgruppeGateway,
            UnleashClient unleashClient,
            InfluxMetricsService influxMetricsService) {
        return new ArbeidssokerService(
                arbeidssokerRepository,
                formidlingsgruppeGateway,
                unleashClient,
                influxMetricsService);
    }

    @Bean
    ArbeidssokerResource arbeidssokerResource(
            ArbeidssokerService arbeidssokerService,
            UserService userService,
            AutorisasjonService autorisasjonService) {
        return new ArbeidssokerResource(arbeidssokerService, userService, autorisasjonService);
    }

    @Bean
    PubliseringAvEventsService publiseringAvEventsService(
            ProfileringRepository profileringRepository,
            BrukerRegistreringRepository brukerRegistreringRepository,
            ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer,
            RegistreringTilstandRepository registreringTilstandRepository,
            ArbeidssokerProfilertProducer arbeidssokerProfilertProducer,
            PrometheusMetricsService prometheusMetricsService) {
        return new PubliseringAvEventsService(
                profileringRepository,
                brukerRegistreringRepository,
                arbeidssokerRegistrertProducer,
                registreringTilstandRepository,
                arbeidssokerProfilertProducer,
                prometheusMetricsService);
    }

    @Bean
    ProfileringService profileringService(ArbeidsforholdGateway arbeidsforholdGateway) {
        return new ProfileringService(arbeidsforholdGateway);
    }

    @Bean
    UserService userService(PdlOppslagGateway pdlOppslagGateway, AuthContextHolder authContextHolder) {
        return new UserService(pdlOppslagGateway, authContextHolder);
    }

    @Bean
    KontaktinfoService kontaktinfoService(
            PdlOppslagGateway pdlOppslagGateway,
            KrrGateway krrGateway) {
        return new KontaktinfoService(pdlOppslagGateway, krrGateway);
    }

    @Bean
    KontaktinfoResource kontaktinfoResource(
            UserService userService,
            KontaktinfoService kontaktinfoService,
            AutorisasjonService autorisasjonService) {
        return new KontaktinfoResource(userService, kontaktinfoService, autorisasjonService);
    }

    @Bean
    TidslinjeAggregator tidslinjeAggregator(
            BrukerRegistreringRepository brukerRegistreringRepository,
            SykmeldtRegistreringRepository sykmeldtRegistreringRepository,
            ReaktiveringRepository reaktiveringRepository,
            ArbeidssokerRepository arbeidssokerRepository) {
        return new TidslinjeAggregator(
                brukerRegistreringRepository,
                sykmeldtRegistreringRepository,
                reaktiveringRepository,
                arbeidssokerRepository);
    }

    @Bean
    TidslinjeResource tidslinjeResource(
            AutorisasjonService autorisasjonService,
            UserService userService,
            TidslinjeAggregator tidslinjeAggregator) {
        return new TidslinjeResource(autorisasjonService, userService, tidslinjeAggregator);
    }

    @Bean
    AutorisasjonService autorisasjonService(Pep veilarbPep, AuthContextHolder authContextHolder) {
        return new AutorisasjonService(veilarbPep, authContextHolder);
    }

    @Bean
    InternalRegistreringStatusoversiktServlet internalRegistreringTilstandServlet(RegistreringTilstandService registreringTilstandService) {
        return new InternalRegistreringStatusoversiktServlet(registreringTilstandService);
    }

    @Bean
    InternalRegistreringStatusServlet internalRegistreringResendingServlet(RegistreringTilstandService registreringTilstandService) {
        return new InternalRegistreringStatusServlet(registreringTilstandService);
    }

    @Bean
    InternalIdentServlet internalIdentServlet(UserService userService) {
        return new InternalIdentServlet(userService);
    }

    @Bean
    InternalArbeidssokerServlet internalArbeidssokerServlet(UserService userService, ArbeidssokerService arbeidssokerService) {
        return new InternalArbeidssokerServlet(userService, arbeidssokerService);
    }

    @Bean
    FeilHandtering feilHandtering() { return new FeilHandtering(); }

    @Bean
    MigreringPostgressResource migreringPostgressResource(MigreringRepositoryImpl migreringRepository, BrukerRegistreringRepository brukerRegistreringRepository) {
        return new MigreringPostgressResource(migreringRepository, brukerRegistreringRepository);
    }
}
