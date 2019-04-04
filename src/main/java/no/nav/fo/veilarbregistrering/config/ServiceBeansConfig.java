package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.security.PepClient;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.db.InfoOmMegRepository;
import no.nav.fo.veilarbregistrering.httpclient.SykmeldtInfoClient;
import no.nav.fo.veilarbregistrering.httpclient.OppfolgingClient;
import no.nav.fo.veilarbregistrering.resources.InfoOmMegResource;
import no.nav.fo.veilarbregistrering.resources.RegistreringResource;
import no.nav.fo.veilarbregistrering.service.*;
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
    InfoOmMegService infoOmMegService(
            ArbeidssokerregistreringRepository arbeidssokerregistreringRepository,
            InfoOmMegRepository infoOmMegRepository
    ) {
        return new InfoOmMegService(
                arbeidssokerregistreringRepository,
                infoOmMegRepository
        );
    }

    @Bean
    BrukerRegistreringService registrerBrukerService(
            ArbeidssokerregistreringRepository arbeidssokerregistreringRepository,
            AktorService aktorService,
            OppfolgingClient oppfolgingClient,
            SykmeldtInfoClient sykeforloepMetadataClient,
            ArbeidsforholdService arbeidsforholdService,
            ManuellRegistreringService manuellRegistreringService,
            StartRegistreringUtils startRegistreringUtils,
            RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature
    ) {
        return new BrukerRegistreringService(
                arbeidssokerregistreringRepository,
                aktorService,
                oppfolgingClient,
                sykeforloepMetadataClient,
                arbeidsforholdService,
                manuellRegistreringService,
                startRegistreringUtils,
                sykemeldtRegistreringFeature
        );
    }

    @Bean
    InfoOmMegResource infoOmMegResource(
            InfoOmMegService infoOmMegService,
            UserService userService,
            AktorService aktorService,
            PepClient pepClient
    ) {
        return new InfoOmMegResource(
                infoOmMegService,
                userService,
                aktorService,
                pepClient
        );
    }

    @Bean
    RegistreringResource registreringResource(
            PepClient pepClient,
            UserService userService,
            ManuellRegistreringService manuellRegistreringService,
            ArbeidsforholdService arbeidsforholdService,
            BrukerRegistreringService brukerRegistreringService,
            RemoteFeatureConfig.TjenesteNedeFeature tjenesteNedeFeature,
            RemoteFeatureConfig.ManuellRegistreringFeature manuellRegistreringFeature
    ) {
        return new RegistreringResource(
                pepClient,
                userService,
                manuellRegistreringService,
                arbeidsforholdService,
                brukerRegistreringService,
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
    InfoOmMegRepository infoOmMegRepository(JdbcTemplate db) {
        return new InfoOmMegRepository(db);
    }

    @Bean
    ArbeidssokerregistreringRepository arbeidssokerregistreringRepository(JdbcTemplate db) {
        return new ArbeidssokerregistreringRepository(db);
    }

    @Bean
    ArbeidsforholdService arbeidsforholdService(ArbeidsforholdV3 arbeidsforholdV3) {
        return new ArbeidsforholdService(arbeidsforholdV3);
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
    HentEnheterService hentEnheterService(OrganisasjonEnhetV2 organisasjonEnhetService) {
        return new HentEnheterService(organisasjonEnhetService);
    }

    @Bean
    EnhetOppslagService enhetOppslagService(HentEnheterService hentEnheterService) {
        return new EnhetOppslagService(hentEnheterService);
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
