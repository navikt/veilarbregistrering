package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class PdlOppslagConfig {

    private final String PDL_PROPERTY_NAME = "PDL_URL";

    @Bean
    PdlOppslagClient pdlOppslagClient(Provider<HttpServletRequest> provider, UnleashService unleashService) {
        return new PdlOppslagClient(getRequiredProperty(PDL_PROPERTY_NAME), provider, unleashService);
    }

    @Bean
    PdlOppslagGateway pdlOppslagGateway(PdlOppslagClient pdlOppslagClient) {
        return new PdlOppslagGatewayImpl(pdlOppslagClient);
    }
}
