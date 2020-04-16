package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class PdlOppslagConfig {

    private final String PDL_PROPERTY_NAME = "PDL_URL";

    @Bean OidcSystemUserTokenProvider oidcSystemUserTokenProvider() {
        return new OidcSystemUserTokenProvider(
                getRequiredProperty("SECURITY_TOKEN_SERVICE_OPENID_CONFIGURATION_URL"),
                getRequiredProperty("SRVVEILARBREGISTRERING_USERNAME"),
                getRequiredProperty("SRVVEILARBREGISTRERING_PASSWORD")
        );
    }

    @Bean
    PdlOppslagClient pdlOppslagClient(Provider<HttpServletRequest> provider, OidcSystemUserTokenProvider oidcSystemUserTokenProvider) {
        return new PdlOppslagClient(getRequiredProperty(PDL_PROPERTY_NAME), provider, oidcSystemUserTokenProvider);
    }

    @Bean
    PdlOppslagGateway pdlOppslagGateway(PdlOppslagClient pdlOppslagClient) {
        return new PdlOppslagGatewayImpl(pdlOppslagClient);
    }
}
