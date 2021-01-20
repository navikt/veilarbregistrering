package no.nav.fo.veilarbregistrering.config;

import no.nav.common.abac.Pep;
import no.nav.common.abac.VeilarbPep;
import no.nav.common.abac.audit.SpringAuditRequestInfoSupplier;
import no.nav.common.utils.Credentials;
import no.nav.common.utils.NaisUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.common.utils.EnvironmentUtils.getRequiredProperty;


@Configuration
public class PepConfig {


    private static final String ABAC_URL_PROPERTY = "ABAC_PDP_ENDPOINT_URL";

    @Bean
    public Pep veilarbPep() {
        Credentials serviceUserCredentials = NaisUtils.getCredentials("service_user");
        return new VeilarbPep(
                getRequiredProperty(ABAC_URL_PROPERTY), serviceUserCredentials.username,
                serviceUserCredentials.password, new SpringAuditRequestInfoSupplier()
        );
    }
}
