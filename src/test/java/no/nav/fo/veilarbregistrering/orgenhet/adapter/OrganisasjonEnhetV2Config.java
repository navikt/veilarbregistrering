package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import no.nav.fo.veilarbregistrering.orgenhet.HentEnheterGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class OrganisasjonEnhetV2Config {

    @Bean
    HentEnheterGateway hentEnheterService() {
        return () -> null;
    }
}
