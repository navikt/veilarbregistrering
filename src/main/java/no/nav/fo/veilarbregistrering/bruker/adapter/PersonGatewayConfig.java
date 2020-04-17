package no.nav.fo.veilarbregistrering.bruker.adapter;

import no.nav.common.oidc.SystemUserTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class PersonGatewayConfig {

    public static final String PERSON_API_PROPERTY_NAME = "VEILARBPERSONAPI_URL";

    @Bean
    VeilArbPersonClient veilArbPersonClient(Provider<HttpServletRequest> provider, SystemUserTokenProvider systemUserTokenProvider) {
        return new VeilArbPersonClient(getRequiredProperty(PERSON_API_PROPERTY_NAME), provider, systemUserTokenProvider);
    }

    @Bean
    PersonGatewayImpl personGateway(VeilArbPersonClient client) {
        return new PersonGatewayImpl(client);
    }
}
