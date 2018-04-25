package no.nav.fo.veilarbregistrering;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;

public class ApplicationConfig implements ApiApplication.NaisApiApplication{

    public static final String APPLICATION_NAME = "veilarbregistrering";

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
        apiAppConfigurator.azureADB2CLogin().sts();
    }

    @Override
    public String getApplicationName() {
        return APPLICATION_NAME;
    }

    @Override
    public Sone getSone() {
        return Sone.SBS;
    }
}
