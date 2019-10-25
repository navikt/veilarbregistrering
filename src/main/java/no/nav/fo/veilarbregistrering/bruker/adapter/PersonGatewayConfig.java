package no.nav.fo.veilarbregistrering.bruker.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

@Configuration
public class PersonGatewayConfig {

    public static final String PERSON_API_PROPERTY_NAME = "VEILARBPERSONGAPI_URL";

    @Value(PERSON_API_PROPERTY_NAME)
    private String baseUrlVeilArbPersonApi;

    @Bean
    VeilArbPersonClient veilArbPersonClient(Provider<HttpServletRequest> provider) {
        return new VeilArbPersonClient(baseUrlVeilArbPersonApi, provider);
    }

    @Bean
    PersonGatewayImpl personGateway(VeilArbPersonClient client) {
        return new PersonGatewayImpl(client);
    }
}
