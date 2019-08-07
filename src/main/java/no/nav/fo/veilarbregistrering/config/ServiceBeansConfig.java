package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient;
import no.nav.fo.veilarbregistrering.orgenhet.EnhetOppslagService;
import no.nav.fo.veilarbregistrering.orgenhet.adapter.HentEnheterGateway;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringService;
import no.nav.fo.veilarbregistrering.registrering.bruker.StartRegistreringUtils;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService;
import no.nav.fo.veilarbregistrering.registrering.resources.RegistreringResource;
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
            ArbeidssokerregistreringRepository arbeidssokerregistreringRepository,
            AktorService aktorService,
            OppfolgingClient oppfolgingClient,
            SykmeldtInfoClient sykeforloepMetadataClient,
            ArbeidsforholdGateway arbeidsforholdGateway,
            ManuellRegistreringService manuellRegistreringService,
            StartRegistreringUtils startRegistreringUtils,
            RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature
    ) {
        return new BrukerRegistreringService(
                arbeidssokerregistreringRepository,
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
    RegistreringResource registreringResource(
            VeilarbAbacPepClient pepClient,
            UserService userService,
            ManuellRegistreringService manuellRegistreringService,
            ArbeidsforholdGateway arbeidsforholdGateway,
            BrukerRegistreringService brukerRegistreringService,
            AktorService aktorService,
            RemoteFeatureConfig.TjenesteNedeFeature tjenesteNedeFeature,
            RemoteFeatureConfig.ManuellRegistreringFeature manuellRegistreringFeature
    ) {
        return new RegistreringResource(
                pepClient,
                userService,
                manuellRegistreringService,
                arbeidsforholdGateway,
                brukerRegistreringService,
                aktorService,
                tjenesteNedeFeature,
                manuellRegistreringFeature
        );
    }

    @Bean
    ManuellRegistreringService manuellRegistreringService(AktorService aktorService,
                                                          ArbeidssokerregistreringRepository arbeidssokerregistreringRepository,
                                                          EnhetOppslagService enhetOppslagService,
                                                          Provider<HttpServletRequest> provider) {
        return new ManuellRegistreringService(aktorService, arbeidssokerregistreringRepository, enhetOppslagService, provider);
    }

    @Bean
    ArbeidssokerregistreringRepository arbeidssokerregistreringRepository(JdbcTemplate db) {
        return new ArbeidssokerregistreringRepository(db);
    }

    @Bean
    ArbeidsforholdGateway arbeidsforholdService(ArbeidsforholdV3 arbeidsforholdV3) {
        return new ArbeidsforholdGateway(arbeidsforholdV3);
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
        return new HentEnheterGateway(organisasjonEnhetService);
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
