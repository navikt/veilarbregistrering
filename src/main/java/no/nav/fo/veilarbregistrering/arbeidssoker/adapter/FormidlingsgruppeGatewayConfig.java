package no.nav.fo.veilarbregistrering.arbeidssoker.adapter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class FormidlingsgruppeGatewayConfig {
    public static final String ARENA_ORDS_TOKEN_PROVIDER = "ARENA_ORDS_TOKEN_PROVIDER";
    public static final String ARENA_ORDS_API = "ARENA_ORDS_API";

    @Bean
    ArenaOrdsTokenProviderClient arenaOrdsTokenProviderClient() {
        return new ArenaOrdsTokenProviderClient(getRequiredProperty(ARENA_ORDS_TOKEN_PROVIDER));
    }

    @Bean
    FormidlingsgruppeRestClient formidlingsgruppeRestClient(ArenaOrdsTokenProviderClient arenaOrdsTokenProviderClient) {
        return new FormidlingsgruppeRestClient(
                getRequiredProperty(ARENA_ORDS_API),
                arenaOrdsTokenProviderClient::getToken);
    }


}
