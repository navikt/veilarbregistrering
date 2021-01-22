package no.nav.fo.veilarbregistrering.config;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.common.abac.Pep;
import no.nav.common.featuretoggle.UnleashService;
import no.nav.common.metrics.MetricsClient;
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
import no.nav.fo.veilarbregistrering.helsesjekk.resources.HelsesjekkResource;
import no.nav.fo.veilarbregistrering.metrics.MetricsService;
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
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstandRepository;
import no.nav.fo.veilarbregistrering.registrering.tilstand.RegistreringTilstandService;
import no.nav.fo.veilarbregistrering.registrering.tilstand.resources.InternalRegistreringStatusServlet;
import no.nav.fo.veilarbregistrering.registrering.tilstand.resources.InternalRegistreringStatusoversiktServlet;
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
            MetricsService metricsService) {
        return new SykemeldingService(sykemeldingGateway, autorisasjonService, metricsService);
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
    RegistreringTilstandService registreringTilstandService(RegistreringTilstandRepository registreringTilstandRepository) {
        return new RegistreringTilstandService(registreringTilstandRepository);
    }

    @Bean
    BrukerTilstandService brukerTilstandService(
            OppfolgingGateway oppfolgingGateway,
            SykemeldingService sykemeldingService,
            UnleashService unleashService) {
        return new BrukerTilstandService(oppfolgingGateway, sykemeldingService, unleashService);
    }

    @Bean
    StartRegistreringStatusService startRegistreringStatusService(
            ArbeidsforholdGateway arbeidsforholdGateway,
            BrukerTilstandService brukerTilstandService,
            PersonGateway personGateway,
            MetricsService metricsService) {
        return new StartRegistreringStatusService(
                arbeidsforholdGateway,
                brukerTilstandService,
                personGateway,
                metricsService);
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
            MetricsService metricsService) {
        return new SykmeldtRegistreringService(
                brukerTilstandService,
                oppfolgingGateway,
                brukerRegistreringRepository,
                manuellRegistreringRepository,
                metricsService);
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
    RegistreringResource registreringResource(
            AutorisasjonService autorisasjonService,
            UserService userService,
            BrukerRegistreringService brukerRegistreringService,
            HentRegistreringService hentRegistreringService,
            UnleashService unleashService,
            StartRegistreringStatusService startRegistreringStatusService,
            SykmeldtRegistreringService sykmeldtRegistreringService,
            InaktivBrukerService inaktivBrukerService,
            MetricsService metricsService) {
        return new RegistreringResource(
                autorisasjonService,
                userService,
                brukerRegistreringService,
                hentRegistreringService,
                unleashService,
                sykmeldtRegistreringService,
                startRegistreringStatusService,
                inaktivBrukerService,
                metricsService);
    }

    @Bean
    HelsesjekkResource helsesjekkResource() {
        return new HelsesjekkResource();
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
            MetricsService metricsService) {
        return new OppgaveService(
                oppgaveGateway,
                oppgaveRepository,
                oppgaveRouter,
                kontaktBrukerHenvendelseProducer,
                metricsService);
    }

    @Bean
    OppgaveRouter oppgaveRouter(
            ArbeidsforholdGateway arbeidsforholdGateway,
            EnhetGateway enhetGateway,
            Norg2Gateway norg2Gateway,
            PersonGateway personGateway,
            PdlOppslagGateway pdlOppslagGateway,
            MetricsService metricsService) {
        return new OppgaveRouter(
                arbeidsforholdGateway,
                enhetGateway,
                norg2Gateway,
                personGateway,
                pdlOppslagGateway,
                metricsService);
    }

    @Bean
    OppgaveResource oppgaveResource(
            Pep pepClient,
            UserService userService,
            OppgaveService oppgaveService) {
        return new OppgaveResource(pepClient, userService, oppgaveService);
    }

    @Bean
    ArbeidssokerService arbeidssokerService(
            ArbeidssokerRepository arbeidssokerRepository,
            FormidlingsgruppeGateway formidlingsgruppeGateway,
            UnleashService unleashService,
            MetricsService metricsService) {
        return new ArbeidssokerService(
                arbeidssokerRepository,
                formidlingsgruppeGateway,
                unleashService,
                metricsService);
    }

    @Bean
    ArbeidssokerResource arbeidssokerResource(
            ArbeidssokerService arbeidssokerService,
            UserService userService,
            Pep pepClient) {
        return new ArbeidssokerResource(arbeidssokerService, userService, pepClient);
    }

    @Bean
    PubliseringAvEventsService publiseringAvEventsService(
            ProfileringRepository profileringRepository,
            BrukerRegistreringRepository brukerRegistreringRepository,
            ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer,
            RegistreringTilstandRepository registreringTilstandRepository,
            ArbeidssokerProfilertProducer arbeidssokerProfilertProducer,
            MetricsService metricsService) {
        return new PubliseringAvEventsService(
                profileringRepository,
                brukerRegistreringRepository,
                arbeidssokerRegistrertProducer,
                registreringTilstandRepository,
                arbeidssokerProfilertProducer,
                metricsService);
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
            Pep pepClient,
            UserService userService,
            KontaktinfoService kontaktinfoService) {
        return new KontaktinfoResource(pepClient, userService, kontaktinfoService);
    }

    @Bean
    AutorisasjonService autorisasjonService(Pep veilarbPep) {
        return new AutorisasjonService(veilarbPep);
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
    MetricsService metricsService(MetricsClient metricsClient, MeterRegistry meterRegistry) {
        return new MetricsService(metricsClient, meterRegistry);
    }
}
