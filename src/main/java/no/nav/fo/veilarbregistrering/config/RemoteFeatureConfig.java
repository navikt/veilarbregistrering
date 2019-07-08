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
    public SykemeldtRegistreringFeature sykemeldtRegistreringFeature(UnleashService unleashService) {
        return new SykemeldtRegistreringFeature(unleashService);
    }

    @Bean
    public TjenesteNedeFeature tjenesteNedeFeature(UnleashService unleashService) {
        return new TjenesteNedeFeature(unleashService);
    }

    @Bean
    public ManuellRegistreringFeature manuellRegistreringFeature(UnleashService unleashService) {
        return new ManuellRegistreringFeature(unleashService);
    }

    public static class SykemeldtRegistreringFeature extends RemoteFeatureConfig {
        protected UnleashService unleashService;
        public SykemeldtRegistreringFeature(UnleashService unleashService) { this.unleashService = unleashService; }

        public boolean erSykemeldtRegistreringAktiv() {
            return unleashService.isEnabled("veilarbregistrering.sykemeldtregistrering");
        }
    }

    public static class TjenesteNedeFeature extends RemoteFeatureConfig {
        protected UnleashService unleashService;
        public TjenesteNedeFeature(UnleashService unleashService) { this.unleashService = unleashService; }

        public boolean erTjenesteNede() {
            return unleashService.isEnabled("arbeidssokerregistrering.nedetid");
        }

    }

    public static class ManuellRegistreringFeature extends RemoteFeatureConfig {
        protected UnleashService unleashService;
        public ManuellRegistreringFeature(UnleashService unleashService) { this.unleashService = unleashService; }

        public boolean skalBrukereBliManueltRegistrert() {
            return unleashService.isEnabled("arbeidssokerregistrering.manuell_registrering");
        }

    }

}
