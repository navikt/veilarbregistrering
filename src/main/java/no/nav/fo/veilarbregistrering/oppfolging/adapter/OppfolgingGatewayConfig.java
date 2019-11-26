package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class OppfolgingGatewayConfig {

    public static final String OPPFOLGING_API_PROPERTY_NAME = "VEILARBOPPFOLGINGAPI_URL";

    @Bean
    OppfolgingClient oppfolgingClient(Provider<HttpServletRequest> provider) {
        return new OppfolgingClient(getRequiredProperty(OPPFOLGING_API_PROPERTY_NAME), provider);
    }

    @Bean
    OppfolgingGateway oppfolgingGateway(OppfolgingClient oppfolgingClient) {
        return new OppfolgingGatewayImpl(oppfolgingClient);
    }
}
