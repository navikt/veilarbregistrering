package no.nav.fo.veilarbregistrering.arbeidssoker.adapter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class FormidlingsgruppeGatewayConfig {
    public static final String ARENA_ORDS_API = "ARENA_ORDS_API";

    @Bean
    FormidlingsgruppeRestClient formidlingsgruppeRestClient() {
        return new FormidlingsgruppeRestClient(
                getRequiredProperty(ARENA_ORDS_API),
                () -> {return null;});
    }


}
