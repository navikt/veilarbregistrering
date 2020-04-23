package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.config.ApiAppConfigurator;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationTestConfig extends ApplicationConfig {

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
       apiAppConfigurator.sts(); //todo: bør endres på sikt slik at bruker logges inn vha devproxy.
    }
}
