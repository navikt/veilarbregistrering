package no.nav.fo.veilarbregistrering.config;

import no.nav.common.featuretoggle.UnleashClient;
import no.nav.common.featuretoggle.UnleashClientImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.common.utils.EnvironmentUtils.getRequiredProperty;
import static no.nav.common.utils.EnvironmentUtils.requireApplicationName;

@Configuration
public class UnleashConfig {
    public static final String UNLEASH_API_URL_PROPERTY = "UNLEASH_API_URL";

    @Bean
    public UnleashClient unleashService() {
        return new UnleashClientImpl(
                requireApplicationName(),
                getRequiredProperty(UNLEASH_API_URL_PROPERTY));
    }
}
