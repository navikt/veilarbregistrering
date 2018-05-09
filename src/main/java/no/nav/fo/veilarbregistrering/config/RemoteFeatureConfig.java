package no.nav.fo.veilarbregistrering.config;

import no.nav.sbl.featuretoggle.remote.RemoteFeatureToggle;
import no.nav.sbl.featuretoggle.remote.RemoteFeatureToggleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RemoteFeatureConfig {


    @Value("${FEATURE_ENDPOINT_URL}")
    private String remoteFeatureUrl;

    @Bean
    public RemoteFeatureToggleRepository remoteFeatureToggleRespository() {
        return new RemoteFeatureToggleRepository(remoteFeatureUrl);
    }

    @Bean
    public RegistreringFeature registrereBrukerGenerellFeature(RemoteFeatureToggleRepository repo) {
        return new RegistreringFeature(repo);
    }

    @Bean
    public OpprettBrukerIArenaFeature registrereBrukerArenaFeature(RemoteFeatureToggleRepository repo) {
        return new OpprettBrukerIArenaFeature(repo);
    }

    public static class RegistreringFeature extends RemoteFeatureToggle {
        public RegistreringFeature(RemoteFeatureToggleRepository repository) {
            super(repository, "veilarboppfolging.registrering", false);
        }
    }

    public static class OpprettBrukerIArenaFeature extends RemoteFeatureToggle {
        public OpprettBrukerIArenaFeature(RemoteFeatureToggleRepository repository) {
            super(repository, "veilarboppfolging.opprettbrukeriarena", false);
        }
    }
}
