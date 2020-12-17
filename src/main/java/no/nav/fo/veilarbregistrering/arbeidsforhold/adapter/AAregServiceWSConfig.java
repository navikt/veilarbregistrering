package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.common.cxf.TimeoutFeature.DEFAULT_CONNECTION_TIMEOUT;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class AAregServiceWSConfig {
    private final static String SOAP_URL = "VIRKSOMHET_ARBEIDSFORHOLD_V3_ENDPOINTURL";
    private final static String REST_URL = "AAREG_REST_API";

    @Bean
    ArbeidsforholdGateway soapArbeidsforholdGateway(ArbeidsforholdV3 arbeidsforholdV3) {
        return new SoapArbeidsforholdGateway(arbeidsforholdV3);
    }

    @Bean
    AaregRestClient aaregRestClient(SystemUserTokenProvider systemUserTokenProvider) {
        return new AaregRestClient(getRequiredProperty(REST_URL), systemUserTokenProvider);
    }

    @Bean
    ArbeidsforholdGateway restArbeidsforholdGateway(AaregRestClient aaregRestClient) {
        return new RestArbeidsforholdGateway(aaregRestClient);
    }

    @Bean
    ArbeidsforholdGateway proxyArbeidsforholdGatway(ArbeidsforholdGateway soapArbeidsforholdGateway, ArbeidsforholdGateway restArbeidsforholdGateway, UnleashService unleashService) {
        return new ProxyArbeidsforholdGateway(soapArbeidsforholdGateway, restArbeidsforholdGateway, unleashService);
    }

    @Bean
    ArbeidsforholdV3 arbeidsforholdV3() {
        return arbeidsforholdV3CXFClient()
                .timeout(DEFAULT_CONNECTION_TIMEOUT, 120000)
                .configureStsForOnBehalfOfWithJWT()
                .build();
    }

    @Bean
    Pingable arbeidsforholdPing() {
        final ArbeidsforholdV3 arbeidsforholdV3Ping = arbeidsforholdV3CXFClient()
                .configureStsForSystemUserInFSS()
                .build();

        Pingable.Ping.PingMetadata metadata = new Pingable.Ping.PingMetadata(
                "ArbeidsforholdV3 via " + SOAP_URL,
                "Ping av ArbeidsforholdV3. Henter informasjon om arbeidsforhold fra aareg.",
                false
        );

        return () -> {
            try {
                arbeidsforholdV3Ping.ping();
                return lyktes(metadata);
            } catch (Exception e) {
                return feilet(metadata, e);
            }
        };
    }

    private static CXFClient<ArbeidsforholdV3> arbeidsforholdV3CXFClient() {
        return new CXFClient<>(ArbeidsforholdV3.class)
                .withOutInterceptor(new LoggingOutInterceptor())
                .address(getRequiredProperty(SOAP_URL));
    }
}