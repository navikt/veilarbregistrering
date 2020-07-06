package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class PdlOppslagConfig {

    private static final String PDL_PROPERTY_NAME = "PDL_URL";

    @Bean
    PdlOppslagClient pdlOppslagClient(SystemUserTokenProvider systemUserTokenProvider) {
        return new PdlOppslagClient(getRequiredProperty(PDL_PROPERTY_NAME), systemUserTokenProvider);
    }

    @Bean
    PdlOppslagGateway pdlOppslagGateway(PdlOppslagClient pdlOppslagClient) {
        return new PdlOppslagGatewayImpl(pdlOppslagClient);
    }

    @Bean
    PdlOppslagClientHelsesjekk pdlOppslagClientHelsesjekk() {
        return new PdlOppslagClientHelsesjekk(getRequiredProperty(PDL_PROPERTY_NAME));
    }
}
