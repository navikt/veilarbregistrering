package no.nav.fo.veilarbregistrering.oppgave.adapter;

import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class OppgaveGatewayConfig {

    public static final String OPPGAVE_PROPERTY_NAME = "OPPGAVE_URL";

    @Bean
    OppgaveRestClient oppgaveRestClient(Provider<HttpServletRequest> provider) {
        return new OppgaveRestClient(getRequiredProperty(OPPGAVE_PROPERTY_NAME), provider);
    }

    @Bean
    OppgaveGateway oppgaveGateway(OppgaveRestClient oppgaveRestClient) {
        return new OppgaveGatewayImpl(oppgaveRestClient);
    }
}
