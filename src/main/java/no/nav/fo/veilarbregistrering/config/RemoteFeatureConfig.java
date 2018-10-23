package no.nav.fo.veilarbregistrering.config;

import no.nav.sbl.featuretoggle.unleash.UnleashService;
import no.nav.sbl.featuretoggle.unleash.UnleashServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;
import static no.nav.sbl.util.EnvironmentUtils.requireApplicationName;

@Configuration
public class RemoteFeatureConfig {
    public static final String UNLEASH_URL_PROPERTY = "UNLEASH_URL";

    @Bean
    public UnleashService unleashService() {
        return new UnleashService(UnleashServiceConfig.builder()
                .applicationName(requireApplicationName())
                .unleashApiUrl(getRequiredProperty(UNLEASH_URL_PROPERTY))
                .build());
    }

    @Bean
    public DigisyfoFeature digiSyfoFeature(UnleashService unleashService) {
        return new DigisyfoFeature(unleashService);
    }

    public static class DigisyfoFeature extends RemoteFeatureConfig {
        protected UnleashService unleashService;
        public DigisyfoFeature(UnleashService unleashService) { this.unleashService = unleashService; }

        public boolean erAktiv() {
            return unleashService.isEnabled("veilarbregistrering.digisyfo");
        }
    }
    
}
