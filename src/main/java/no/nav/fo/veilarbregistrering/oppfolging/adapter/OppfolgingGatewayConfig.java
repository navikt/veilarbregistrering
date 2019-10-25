package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

@Configuration
public class OppfolgingGatewayConfig {

    public static final String OPPFOLGING_API_PROPERTY_NAME = "VEILARBOPPFOLGINGAPI_URL";

    @Value(OPPFOLGING_API_PROPERTY_NAME)
    private String baseUrlVeilArbOppfolgingApi;

    @Bean
    OppfolgingClient oppfolgingClient(Provider<HttpServletRequest> provider) {
        return new OppfolgingClient(baseUrlVeilArbOppfolgingApi, provider);
    }

    @Bean
    OppfolgingGateway oppfolgingGateway(OppfolgingClient oppfolgingClient) {
        return new OppfolgingGatewayImpl(oppfolgingClient);
    }
}
