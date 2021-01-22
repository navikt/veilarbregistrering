package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ArbeidsforholdGatewayConfig {

    @Bean
    ArbeidsforholdGateway arbeidsforholdGateway() {
        return new ArbeidsforholdGatewayImpl(new StubAaregRestClient());
    }

}
