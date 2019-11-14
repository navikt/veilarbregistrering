package no.nav.fo.veilarbregistrering.oppgave.adapter;

import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class OppgaveGatewayConfig {

    @Bean
    OppgaveRestClient oppgaveRestClient(Provider<HttpServletRequest> provider) {
        return new OppgaveRestClient(oppgaveUrl(getRequiredProperty("FASIT_ENVIRONMENT_NAME")), provider);
    }

    @Bean
    OppgaveGateway oppgaveGateway(OppgaveRestClient oppgaveRestClient) {
        return new OppgaveGatewayImpl(oppgaveRestClient);
    }

    private String oppgaveUrl(String namespace) {
        if ("p".equals(namespace)) {
            return "https://oppgave.nais.adeo.no/api/v1";
        }
        return "https://oppgave.nais.preprod.local/api/v1";
    }
}
