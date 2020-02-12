package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.ArbeidsforholdGatewayImpl;
import no.nav.fo.veilarbregistrering.arbeidsforhold.resources.ArbeidsforholdResource;
import no.nav.fo.veilarbregistrering.bruker.PersonGateway;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.oppgave.KontaktBrukerHenvendelseProducer;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveService;
import no.nav.fo.veilarbregistrering.oppgave.resources.OppgaveResource;
import no.nav.fo.veilarbregistrering.orgenhet.HentEnheterGateway;
import no.nav.fo.veilarbregistrering.orgenhet.adapter.HentEnheterGatewayImpl;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.StartRegistreringUtils;
import no.nav.fo.veilarbregistrering.profilering.db.ProfileringRepositoryImpl;
import no.nav.fo.veilarbregistrering.registrering.bruker.ArbeidssokerRegistrertProducer;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringService;
import no.nav.fo.veilarbregistrering.registrering.bruker.db.BrukerRegistreringRepositoryImpl;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService;
import no.nav.fo.veilarbregistrering.registrering.manuell.db.ManuellRegistreringRepositoryImpl;
import no.nav.fo.veilarbregistrering.registrering.resources.RegistreringResource;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingGateway;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.resources.SykemeldingResource;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.OrganisasjonEnhetV2;
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
            //FIXME: Overflødig - metodene kan være static
            StartRegistreringUtils startRegistreringUtils,
            RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature,
            ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer
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
                sykemeldtRegistreringFeature,
                arbeidssokerRegistrertProducer
        );
    }

    @Bean
    RegistreringResource registreringResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            ManuellRegistreringService manuellRegistreringService,
            BrukerRegistreringService brukerRegistreringService,
            AktorService aktorService,
            RemoteFeatureConfig.TjenesteNedeFeature tjenesteNedeFeature,
            RemoteFeatureConfig.ManuellRegistreringFeature manuellRegistreringFeature,
            Provider<HttpServletRequest> provider
    ) {
        return new RegistreringResource(
                pepClient,
                userService,
                manuellRegistreringService,
                brukerRegistreringService,
                aktorService,
                tjenesteNedeFeature,
                manuellRegistreringFeature,
                provider
        );
    }

    @Bean
    ArbeidsforholdResource arbeidsforholdResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            ArbeidsforholdGateway arbeidsforholdGateway,
            AktorService aktorService
    ) {
        return new ArbeidsforholdResource(
                pepClient,
                userService,
                arbeidsforholdGateway,
                aktorService
        );
    }

    @Bean
    SykemeldingResource sykemeldingResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            SykemeldingService sykemeldingService,
            AktorService aktorService
    ) {
        return new SykemeldingResource(
                pepClient,
                userService,
                sykemeldingService,
                aktorService
        );
    }

    @Bean
    OppgaveService oppgaveService(OppgaveGateway oppgaveGateway, PersonGateway personGateway, KontaktBrukerHenvendelseProducer kontaktBrukerHenvendelseProducer) {
        return new OppgaveService(oppgaveGateway, personGateway, kontaktBrukerHenvendelseProducer);
    }

    @Bean
    OppgaveResource oppgaveResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            OppgaveService oppgaveService,
            AktorService aktorService
    ) {
        return new OppgaveResource(pepClient, userService, oppgaveService, aktorService);
    }

    @Bean
    ManuellRegistreringService manuellRegistreringService(ManuellRegistreringRepository manuellRegistreringRepository,
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
    ArbeidsforholdGateway arbeidsforholdGateway(ArbeidsforholdV3 arbeidsforholdV3) {
        return new ArbeidsforholdGatewayImpl(arbeidsforholdV3);
    }

    @Bean
    HentEnheterGateway hentEnheterService(OrganisasjonEnhetV2 organisasjonEnhetService) {
        return new HentEnheterGatewayImpl(organisasjonEnhetService);
    }

    //FIXME: Overflødig - metodene kan være static
    @Bean
    StartRegistreringUtils startRegistreringUtils() {
        return new StartRegistreringUtils();
    }

    @Bean
    UserService userService(Provider<HttpServletRequest> provider) {
        return new UserService(provider);
    }

}
