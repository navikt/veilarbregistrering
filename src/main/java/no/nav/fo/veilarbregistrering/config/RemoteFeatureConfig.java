package no.nav.fo.veilarbregistrering.config;

import no.nav.sbl.featuretoggle.unleash.UnleashService;
import no.nav.sbl.featuretoggle.unleash.UnleashServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;
import static no.nav.sbl.util.EnvironmentUtils.requireApplicationName;

@Configuration
public class RemoteFeatureConfig {
    public static final String UNLEASH_API_URL_PROPERTY = "UNLEASH_API_URL";

    @Bean
    public UnleashService unleashService() {
        return new UnleashService(UnleashServiceConfig.builder()
                .applicationName(requireApplicationName())
                .unleashApiUrl(getRequiredProperty(UNLEASH_API_URL_PROPERTY))
                .build());
    }

    @Bean
    public SykemeldtRegistreringFeature digiSyfoFeature(UnleashService unleashService) {
        return new SykemeldtRegistreringFeature(unleashService);
    }

    public static class SykemeldtRegistreringFeature extends RemoteFeatureConfig {
        protected UnleashService unleashService;
        public SykemeldtRegistreringFeature(UnleashService unleashService) { this.unleashService = unleashService; }

        public boolean erSykemeldtRegistreringAktiv() {
            return unleashService.isEnabled("veilarbregistrering.sykemeldtregistrering");
        }

        public boolean skalKalleDigisyfoTjeneste() {
            return unleashService.isEnabled("veilarbregistrering.digisyfo.mock");
        }

        public boolean skalMockeDataFraDigisyfo() {
            return unleashService.isEnabled("veilarbregistrering.digisyfo");
        }
    }
    
}
