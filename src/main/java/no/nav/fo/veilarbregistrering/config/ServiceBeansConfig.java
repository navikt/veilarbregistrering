package no.nav.fo.veilarbregistrering.config;

import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.resources.RegistreringResource;
import no.nav.fo.veilarbregistrering.service.ArbeidsforholdService;
import no.nav.fo.veilarbregistrering.service.BrukerRegistreringService;
import no.nav.fo.veilarbregistrering.service.OppfolgingService;
import no.nav.fo.veilarbregistrering.service.UserService;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ServiceBeansConfig {


    @Bean
    BrukerRegistreringService registrerBrukerService(
            ArbeidssokerregistreringRepository arbeidssokerregistreringRepository,
            AktorService aktorService,
            RemoteFeatureConfig.OpprettBrukerIArenaFeature sjekkRegistrereBrukerArenaFeature,
            RemoteFeatureConfig.RegistreringFeature skalRegistrereBrukerGenerellFeature,
            OppfolgingService oppfolgingService,
            ArbeidsforholdService arbeidsforholdService)
    {
        return new BrukerRegistreringService(
                arbeidssokerregistreringRepository,
                aktorService,
                sjekkRegistrereBrukerArenaFeature,
                skalRegistrereBrukerGenerellFeature,
                oppfolgingService,
                arbeidsforholdService
        );
    }

    @Bean
    RegistreringResource registreringResource() {
        return new RegistreringResource();
    }

    @Bean
    ArbeidssokerregistreringRepository arbeidssokerregistreringRepository(JdbcTemplate db) {
        return new ArbeidssokerregistreringRepository(db);
    }

    @Bean
    OppfolgingService oppfolgingService() {
        return new OppfolgingService();
    }

    @Bean
    ArbeidsforholdService arbeidsforholdService(ArbeidsforholdV3 arbeidsforholdV3) {
        return new ArbeidsforholdService(arbeidsforholdV3);
    }

    @Bean
    UserService userService() {
        return new UserService();
    }

}
