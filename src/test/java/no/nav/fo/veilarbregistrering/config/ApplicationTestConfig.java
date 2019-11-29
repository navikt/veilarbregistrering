package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.mock.*;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;

@Configuration
public class ApplicationTestConfig extends ApplicationConfig {

    public static final boolean RUN_WITH_MOCKS = true;

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
       if (RUN_WITH_MOCKS) {
           apiAppConfigurator.sts(); //todo: bør endres på sikt slik at bruker logges inn vha devproxy.
       } else {
           apiAppConfigurator.azureADB2CLogin().sts();
       }
    }

    @Bean
    @Conditional(Mock.class)
    public AktorService aktorService() {
        return new AktorServiceMock();
    }

    @Bean
    @Conditional(Mock.class)
    public ArbeidsforholdGateway arbeidsforholdService() {
        return new ArbeidsforholdGatewayMock();
    }

    @Bean
    @Conditional(Mock.class)
    public UserService userService(Provider<HttpServletRequest> requestProvider) {
        return new UserServiceMock(requestProvider);
    }

    @Bean
    @Conditional(Mock.class)
    public VeilarbAbacPepClient pepClient() {
        return mock(VeilarbAbacPepClient.class);
    }

    @Bean
    @Conditional(Mock.class)
    public OppfolgingClientMock oppfolgingClient() {
        return new OppfolgingClientMock();
    }

    @Bean
    @Conditional(Mock.class)
    public UnleashService unleashService() {
        return new UnleashServiceMock();
    }
}
