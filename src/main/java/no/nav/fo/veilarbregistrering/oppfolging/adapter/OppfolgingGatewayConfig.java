package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.config.GammelSystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
@Import(OppfolgingClientHelseSjekk.class)
public class OppfolgingGatewayConfig {

    public static final String OPPFOLGING_API_PROPERTY_NAME = "VEILARBOPPFOLGINGAPI_URL";

    @Bean
    OppfolgingClient oppfolgingClient(
            Provider<HttpServletRequest> provider,
            SystemUserTokenProvider systemUserTokenProvider,
            GammelSystemUserTokenProvider gammelSystemUserTokenProvider,
            UnleashService unleashService) {
        return new OppfolgingClient(
                getRequiredProperty(OPPFOLGING_API_PROPERTY_NAME),
                provider, systemUserTokenProvider,
                gammelSystemUserTokenProvider,
                unleashService);
    }

    @Bean
    OppfolgingGateway oppfolgingGateway(OppfolgingClient oppfolgingClient) {
        return new OppfolgingGatewayImpl(oppfolgingClient);
    }
}
