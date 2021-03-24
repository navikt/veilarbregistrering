package no.nav.fo.veilarbregistrering.config;

import no.nav.common.abac.Pep;
import no.nav.common.featuretoggle.UnleashService;
import no.nav.common.health.selftest.SelfTestChecks;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.resources.ArbeidsforholdResource;
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerRepository;
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerService;
import no.nav.fo.veilarbregistrering.arbeidssoker.FormidlingsgruppeGateway;
import no.nav.fo.veilarbregistrering.arbeidssoker.resources.ArbeidssokerResource;
import no.nav.fo.veilarbregistrering.arbeidssoker.resources.InternalArbeidssokerServlet;
import no.nav.fo.veilarbregistrering.autorisasjon.AutorisasjonService;
import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.fo.veilarbregistrering.bruker.resources.InternalIdentServlet;
import no.nav.fo.veilarbregistrering.bruker.resources.KontaktinfoResource;
import no.nav.fo.veilarbregistrering.enhet.EnhetGateway;
import no.nav.fo.veilarbregistrering.feil.FeilHandtering;
import no.nav.fo.veilarbregistrering.helsesjekk.resources.HelsesjekkResource;
import no.nav.fo.veilarbregistrering.metrics.InfluxMetricsService;
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.oppgave.*;
import no.nav.fo.veilarbregistrering.oppgave.resources.OppgaveResource;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.ProfileringService;
import no.nav.fo.veilarbregistrering.registrering.bruker.*;
import no.nav.fo.veilarbregistrering.registrering.bruker.resources.RegistreringResource;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerProfilertProducer;
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertProducer;
import no.nav.fo.veilarbregistrering.registrering.publisering.PubliseringAvEventsService;
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringFormidlingRepository;
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringFormidlingService;
import no.nav.fo.veilarbregistrering.registrering.formidling.resources.InternalRegistreringStatusServlet;
import no.nav.fo.veilarbregistrering.registrering.formidling.resources.InternalRegistreringStatusoversiktServlet;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingGateway;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.resources.SykemeldingResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceBeansConfig {

    @Bean
    SykemeldingService sykemeldingService(
            SykemeldingGateway sykemeldingGateway,
            AutorisasjonService autorisasjonService,
            InfluxMetricsService influxMetricsService) {
        return new SykemeldingService(sykemeldingGateway, autorisasjonService, influxMetricsService);
    }

    @Bean
    HentRegistreringService hentRegistreringService(
            BrukerRegistreringRepository brukerRegistreringRepository,
            ProfileringRepository profileringRepository,
            ManuellRegistreringRepository manuellRegistreringRepository,
            Norg2Gateway norg2Gateway) {
        return new HentRegistreringService(
                brukerRegistreringRepository,
                profileringRepository,
                manuellRegistreringRepository,
                norg2Gateway);
    }

    @Bean
    RegistreringFormidlingService registreringTilstandService(RegistreringFormidlingRepository registreringFormidlingRepository) {
        return new RegistreringFormidlingService(registreringFormidlingRepository);
    }

    @Bean
    BrukerTilstandService brukerTilstandService(
            OppfolgingGateway oppfolgingGateway,
            SykemeldingService sykemeldingService,
            UnleashService unleashService,
            BrukerRegistreringRepository brukerRegistreringRepository) {
        return new BrukerTilstandService(oppfolgingGateway, sykemeldingService, unleashService, brukerRegistreringRepository);
    }

    @Bean
    StartRegistreringStatusService startRegistreringStatusService(
            ArbeidsforholdGateway arbeidsforholdGateway,
            BrukerTilstandService brukerTilstandService,
            PersonGateway personGateway,
            InfluxMetricsService influxMetricsService) {
        return new StartRegistreringStatusService(
                arbeidsforholdGateway,
                brukerTilstandService,
                personGateway,
                influxMetricsService);
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
            BrukerTilstandService brukerTilstandService,
            OppfolgingGateway oppfolgingGateway,
            BrukerRegistreringRepository brukerRegistreringRepository,
            ManuellRegistreringRepository manuellRegistreringRepository,
            InfluxMetricsService influxMetricsService) {
        return new SykmeldtRegistreringService(
                brukerTilstandService,
                oppfolgingGateway,
                brukerRegistreringRepository,
                manuellRegistreringRepository,
                influxMetricsService);
    }

    @Bean
    BrukerRegistreringService registrerBrukerService(
            BrukerRegistreringRepository brukerRegistreringRepository,
            ProfileringRepository profileringRepository,
            OppfolgingGateway oppfolgingGateway,
            ProfileringService profileringService,
            RegistreringFormidlingRepository registreringFormidlingRepository,
            BrukerTilstandService brukerTilstandService,
            ManuellRegistreringRepository manuellRegistreringRepository,
            InfluxMetricsService influxMetricsService) {
        return new BrukerRegistreringService(
                brukerRegistreringRepository,
                profileringRepository,
                oppfolgingGateway,
                profileringService,
                registreringFormidlingRepository,
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
            UnleashService unleashService,
            StartRegistreringStatusService startRegistreringStatusService,
            SykmeldtRegistreringService sykmeldtRegistreringService,
            InaktivBrukerService inaktivBrukerService,
            InfluxMetricsService influxMetricsService) {
        return new RegistreringResource(
                autorisasjonService,
                userService,
                brukerRegistreringService,
                hentRegistreringService,
                unleashService,
                sykmeldtRegistreringService,
                startRegistreringStatusService,
                inaktivBrukerService,
                influxMetricsService);
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
            KontaktBrukerHenvendelseProducer kontaktBrukerHenvendelseProducer,
            InfluxMetricsService influxMetricsService) {
        return new OppgaveService(
                oppgaveGateway,
                oppgaveRepository,
                oppgaveRouter,
                kontaktBrukerHenvendelseProducer,
                influxMetricsService);
    }

    @Bean
    OppgaveRouter oppgaveRouter(
            ArbeidsforholdGateway arbeidsforholdGateway,
            EnhetGateway enhetGateway,
            Norg2Gateway norg2Gateway,
            PersonGateway personGateway,
            PdlOppslagGateway pdlOppslagGateway,
            InfluxMetricsService influxMetricsService) {
        return new OppgaveRouter(
                arbeidsforholdGateway,
                enhetGateway,
                norg2Gateway,
                personGateway,
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
            UnleashService unleashService,
            InfluxMetricsService influxMetricsService) {
        return new ArbeidssokerService(
                arbeidssokerRepository,
                formidlingsgruppeGateway,
                unleashService,
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
            RegistreringFormidlingRepository registreringFormidlingRepository,
            ArbeidssokerProfilertProducer arbeidssokerProfilertProducer,
            PrometheusMetricsService prometheusMetricsService) {
        return new PubliseringAvEventsService(
                profileringRepository,
                brukerRegistreringRepository,
                arbeidssokerRegistrertProducer,
                registreringFormidlingRepository,
                arbeidssokerProfilertProducer,
                prometheusMetricsService);
    }

    @Bean
    ProfileringService profileringService(ArbeidsforholdGateway arbeidsforholdGateway) {
        return new ProfileringService(arbeidsforholdGateway);
    }

    @Bean
    UserService userService(PdlOppslagGateway pdlOppslagGateway) {
        return new UserService(pdlOppslagGateway);
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
    AutorisasjonService autorisasjonService(Pep veilarbPep) {
        return new AutorisasjonService(veilarbPep);
    }

    @Bean
    InternalRegistreringStatusoversiktServlet internalRegistreringTilstandServlet(RegistreringFormidlingService registreringFormidlingService) {
        return new InternalRegistreringStatusoversiktServlet(registreringFormidlingService);
    }

    @Bean
    InternalRegistreringStatusServlet internalRegistreringResendingServlet(RegistreringFormidlingService registreringFormidlingService) {
        return new InternalRegistreringStatusServlet(registreringFormidlingService);
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
}
