package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.apiapp.security.PepClient;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.mock.*;
import no.nav.fo.veilarbregistrering.service.ArbeidsforholdService;
import no.nav.fo.veilarbregistrering.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

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
    public ArbeidsforholdService arbeidsforholdService() {
        return new ArbeidsforholdServiceMock();
    }

    @Bean
    @Conditional(Mock.class)
    public UserService userService(Provider<HttpServletRequest> requestProvider) {
        return new UserServiceMock(requestProvider);
    }

    @Bean
    @Conditional(Mock.class)
    public PepClient pepClient() {
        return new PepClientMock();
    }

    @Bean
    @Conditional(Mock.class)
    public OppfolgingClientMock oppfolgingClient() {
        return new OppfolgingClientMock();
    }

    @Bean
    @Conditional(Mock.class)
    public DigisyfoClientMock sykeforloepMetadataClient() {
        return new DigisyfoClientMock();
    }

}
