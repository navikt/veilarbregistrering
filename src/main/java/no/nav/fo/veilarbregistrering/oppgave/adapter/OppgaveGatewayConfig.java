package no.nav.fo.veilarbregistrering.oppgave.adapter;

import no.nav.common.sts.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.common.utils.EnvironmentUtils.getRequiredProperty;

@Configuration
public class OppgaveGatewayConfig {

    public static final String OPPGAVE_PROPERTY_NAME = "OPPGAVE_URL";

    @Bean
    OppgaveRestClient oppgaveRestClient(SystemUserTokenProvider systemUserTokenProvider) {
        return new OppgaveRestClient(getRequiredProperty(OPPGAVE_PROPERTY_NAME), systemUserTokenProvider);
    }

    @Bean
    OppgaveGateway oppgaveGateway(OppgaveRestClient oppgaveRestClient) {
        return new OppgaveGatewayImpl(oppgaveRestClient);
    }
}
