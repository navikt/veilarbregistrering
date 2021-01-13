package no.nav.fo.veilarbregistrering.bruker.krr;

import no.nav.common.sts.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.KrrGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.common.utils.EnvironmentUtils.getRequiredProperty;

@Configuration
public class KrrConfig {

    private static final String KRR_PROPERTY_NAME = "KRR_BASE_URL";

    @Bean
    KrrClient krrClient(SystemUserTokenProvider systemUserTokenProvider) {
        return new KrrClient(getRequiredProperty(KRR_PROPERTY_NAME), systemUserTokenProvider);
    }

    @Bean
    KrrGateway krrGateway(KrrClient krrClient) {
        return new KrrGatewayImpl(krrClient);
    }
}
