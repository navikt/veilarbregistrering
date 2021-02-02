package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.common.sts.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.common.utils.EnvironmentUtils.getRequiredProperty;


@Configuration
public class PdlOppslagConfig {

    public static final String PDL_PROPERTY_NAME = "PDL_URL";

    @Bean
    PdlOppslagClient pdlOppslagClient(SystemUserTokenProvider systemUserTokenProvider) {
        return new PdlOppslagClient(getRequiredProperty(PDL_PROPERTY_NAME), systemUserTokenProvider);
    }

    @Bean
    PdlOppslagGateway pdlOppslagGateway(PdlOppslagClient pdlOppslagClient) {
        return new PdlOppslagGatewayImpl(pdlOppslagClient);
    }
}
