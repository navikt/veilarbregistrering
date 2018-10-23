package no.nav.fo.veilarbregistrering.config;

import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RemoteFeatureConfig {

    protected UnleashService unleashService;

    @Bean
    public DigisyfoFeature digiSyfoFeature(UnleashService unleashService) {
        return new DigisyfoFeature(unleashService);
    }

    public static class DigisyfoFeature extends RemoteFeatureConfig {
        public DigisyfoFeature(UnleashService unleashService) { this.unleashService = unleashService; }

        public boolean erAktiv() {
            return unleashService.isEnabled("veilarbregistrering.digisyfo");
        }
    }
    
}
