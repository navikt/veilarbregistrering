package no.nav.fo.veilarbregistrering.bruker.adapter;


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
    PersonGatewayImpl personGateway(VeilArbPersonClient client) {
        return new PersonGatewayImpl(client);
    }
}
