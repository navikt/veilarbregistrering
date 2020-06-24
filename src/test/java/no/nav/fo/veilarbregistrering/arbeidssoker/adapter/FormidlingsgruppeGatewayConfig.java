package no.nav.fo.veilarbregistrering.arbeidssoker.adapter;

import no.nav.fo.veilarbregistrering.arbeidssoker.FormidlingsgruppeGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FormidlingsgruppeGatewayConfig {

    @Bean
    FormidlingsgruppeRestClient formidlingsgruppeRestClient() {
        return new FormidlingsgruppeRestClientMock();
    }

    @Bean
    FormidlingsgruppeGateway formidlingsgruppeGateway(
            FormidlingsgruppeRestClient formidlingsgruppeRestClient) {
        return new FormidlingsgruppeGatewayImpl(formidlingsgruppeRestClient);
    }
}
