package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.common.sts.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.metrics.InfluxMetricsService;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.common.utils.EnvironmentUtils.getRequiredProperty;

@Configuration
public class OppfolgingGatewayConfig {

    public static final String OPPFOLGING_API_PROPERTY_NAME = "VEILARBOPPFOLGINGAPI_URL";

    @Bean
    OppfolgingClient oppfolgingClient(
            InfluxMetricsService influxMetricsService,
            ObjectMapper objectMapper,
            SystemUserTokenProvider systemUserTokenProvider) {
        return new OppfolgingClient(
                influxMetricsService,
                objectMapper,
                getRequiredProperty(OPPFOLGING_API_PROPERTY_NAME),
                systemUserTokenProvider);
    }

    @Bean
    OppfolgingGateway oppfolgingGateway(OppfolgingClient oppfolgingClient) {
        return new OppfolgingGatewayImpl(oppfolgingClient);
    }
}
