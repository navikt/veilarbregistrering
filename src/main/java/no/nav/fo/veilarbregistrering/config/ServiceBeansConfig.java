package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.ArbeidsforholdGatewayImpl;
import no.nav.fo.veilarbregistrering.arbeidsforhold.resources.ArbeidsforholdResource;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.orgenhet.adapter.HentEnheterGatewayImpl;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.db.ProfileringRepositoryImpl;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.bruker.db.BrukerRegistreringRepositoryImpl;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.manuell.db.ManuellRegistreringRepositoryImpl;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient;
import no.nav.fo.veilarbregistrering.orgenhet.EnhetOppslagService;
import no.nav.fo.veilarbregistrering.orgenhet.HentEnheterGateway;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringService;
import no.nav.fo.veilarbregistrering.registrering.bruker.StartRegistreringUtils;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService;
import no.nav.fo.veilarbregistrering.registrering.resources.RegistreringResource;
import no.nav.fo.veilarbregistrering.sykemelding.resources.SykemeldingResource;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.binding.OrganisasjonEnhetV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

@Configuration
public class ServiceBeansConfig {


    @Bean
    BrukerRegistreringService registrerBrukerService(
            BrukerRegistreringRepository brukerRegistreringRepository,
            ProfileringRepository profileringRepository,
            AktorService aktorService,
            OppfolgingClient oppfolgingClient,
            SykmeldtInfoClient sykeforloepMetadataClient,
            ArbeidsforholdGateway arbeidsforholdGateway,
            ManuellRegistreringService manuellRegistreringService,
            StartRegistreringUtils startRegistreringUtils,
            RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature
    ) {
        return new BrukerRegistreringService(
                brukerRegistreringRepository,
                profileringRepository,
                aktorService,
                oppfolgingClient,
                sykeforloepMetadataClient,
                arbeidsforholdGateway,
                manuellRegistreringService,
                startRegistreringUtils,
                sykemeldtRegistreringFeature
        );
    }

    @Bean
    RegistreringResource arbeidsforholdResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            ManuellRegistreringService manuellRegistreringService,
            BrukerRegistreringService brukerRegistreringService,
            AktorService aktorService,
            RemoteFeatureConfig.TjenesteNedeFeature tjenesteNedeFeature,
            RemoteFeatureConfig.ManuellRegistreringFeature manuellRegistreringFeature
    ) {
        return new RegistreringResource(
                pepClient,
                userService,
                manuellRegistreringService,
                brukerRegistreringService,
                aktorService,
                tjenesteNedeFeature,
                manuellRegistreringFeature
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
    SykemeldingResource arbeidsforholdResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            BrukerRegistreringService brukerRegistreringService,
            AktorService aktorService
    ) {
        return new SykemeldingResource(
                pepClient,
                userService,
                brukerRegistreringService,
                aktorService
        );
    }

    @Bean
    ManuellRegistreringService manuellRegistreringService(ManuellRegistreringRepository manuellRegistreringRepository,
                                                          EnhetOppslagService enhetOppslagService,
                                                          Provider<HttpServletRequest> provider) {
        return new ManuellRegistreringService(manuellRegistreringRepository, enhetOppslagService, provider);
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
    ArbeidsforholdGateway arbeidsforholdService(ArbeidsforholdV3 arbeidsforholdV3) {
        return new ArbeidsforholdGatewayImpl(arbeidsforholdV3);
    }

    @Bean
    OppfolgingClient oppfolgingClient(Provider<HttpServletRequest> provider) {
        return new OppfolgingClient(provider);
    }

    @Bean
    SykmeldtInfoClient sykeforloepMetadataClient(Provider<HttpServletRequest> provider) {
        return new SykmeldtInfoClient(provider);
    }

    @Bean
    HentEnheterGateway hentEnheterService(OrganisasjonEnhetV2 organisasjonEnhetService) {
        return new HentEnheterGatewayImpl(organisasjonEnhetService);
    }

    @Bean
    EnhetOppslagService enhetOppslagService(HentEnheterGateway hentEnheterGateway) {
        return new EnhetOppslagService(hentEnheterGateway);
    }

    @Bean
    StartRegistreringUtils startRegistreringUtils() {
        return new StartRegistreringUtils();
    }

    @Bean
    UserService userService(Provider<HttpServletRequest> provider) {
        return new UserService(provider);
    }

}
