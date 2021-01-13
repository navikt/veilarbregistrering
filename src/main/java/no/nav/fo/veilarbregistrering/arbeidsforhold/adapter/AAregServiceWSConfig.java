package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class AAregServiceWSConfig {

    private final static String REST_URL = "AAREG_REST_API";

    @Bean
    AaregRestClient aaregRestClient(SystemUserTokenProvider systemUserTokenProvider) {
        return new AaregRestClient(getRequiredProperty(REST_URL), systemUserTokenProvider);
    }

    @Bean
    ArbeidsforholdGateway restArbeidsforholdGateway(AaregRestClient aaregRestClient) {
        return new RestArbeidsforholdGateway(aaregRestClient);
    }
}