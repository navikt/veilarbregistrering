package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.common.auth.context.AuthContextHolder;
import no.nav.common.sts.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.common.utils.EnvironmentUtils.getRequiredProperty;


@Configuration
public class ArbeidsforholdGatewayConfig {
    private final static String REST_URL = "AAREG_REST_API";

    @Bean
    AaregRestClient aaregRestClient(
            SystemUserTokenProvider systemUserTokenProvider,
            AuthContextHolder authContextHolder) {
        return new AaregRestClient(
                getRequiredProperty(REST_URL),
                systemUserTokenProvider,
                authContextHolder);
    }

    @Bean
    ArbeidsforholdGateway arbeidsforholdGateway(AaregRestClient aaregRestClient) {
        return new ArbeidsforholdGatewayImpl(aaregRestClient);
    }
}