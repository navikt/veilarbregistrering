package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.config.ApiAppConfigurator;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationTestConfig extends ApplicationConfig {

    public static final boolean RUN_WITH_MOCKS = true;

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
       if (RUN_WITH_MOCKS) {
           apiAppConfigurator.sts(); //todo: bør endres på sikt slik at bruker logges inn vha devproxy.
       } else {
           apiAppConfigurator.azureADB2CLogin().sts();
       }
    }

}
