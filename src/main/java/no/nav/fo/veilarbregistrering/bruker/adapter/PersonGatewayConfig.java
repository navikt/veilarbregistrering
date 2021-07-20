package no.nav.fo.veilarbregistrering.bruker.adapter;


import no.nav.common.featuretoggle.UnleashClient;
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.common.utils.EnvironmentUtils.getRequiredProperty;


@Configuration
public class PersonGatewayConfig {

    public static final String PERSON_API_PROPERTY_NAME = "VEILARBPERSONAPI_URL";

    @Bean
    VeilArbPersonClient veilArbPersonClient() {
        return new VeilArbPersonClient(getRequiredProperty(PERSON_API_PROPERTY_NAME));
    }

    @Bean
    PersonGatewayImpl personGateway(PdlOppslagGateway pdlOppslagGateway) {
        return new PersonGatewayImpl(pdlOppslagGateway);
    }
}
