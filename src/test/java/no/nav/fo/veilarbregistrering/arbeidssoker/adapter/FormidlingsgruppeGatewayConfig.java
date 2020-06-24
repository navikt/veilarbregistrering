package no.nav.fo.veilarbregistrering.arbeidssoker.adapter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FormidlingsgruppeGatewayConfig {

    @Bean
    FormidlingsgruppeRestClient formidlingsgruppeRestClient() {
        return new FormidlingsgruppeRestClientMock();
    }
}
