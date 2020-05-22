import no.nav.apiapp.ApiApp;
import no.nav.common.utils.NaisUtils;
import no.nav.fo.veilarbregistrering.config.ApplicationConfig;

import static java.lang.System.setProperty;
import static no.nav.dialogarena.aktor.AktorConfig.AKTOER_ENDPOINT_URL;
import static no.nav.fo.veilarbregistrering.db.DatabaseConfig.VEILARBREGISTRERINGDB_PASSWORD;
import static no.nav.fo.veilarbregistrering.db.DatabaseConfig.VEILARBREGISTRERINGDB_USERNAME;
import static no.nav.metrics.MetricsConfig.SENSU_BATCHES_PER_SECOND_PROPERTY_NAME;
import static no.nav.sbl.dialogarena.common.abac.pep.service.AbacServiceConfig.ABAC_ENDPOINT_URL_PROPERTY_NAME;
import static no.nav.sbl.dialogarena.common.cxf.StsSecurityConstants.STS_URL_KEY;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class Main {

    public static void main(String... args) throws Exception {

        NaisUtils.Credentials serviceuser_creds = NaisUtils.getCredentials("serviceuser_creds");
        setProperty("SRVVEILARBREGISTRERING_USERNAME", serviceuser_creds.username);
        setProperty("SRVVEILARBREGISTRERING_PASSWORD", serviceuser_creds.password);

        NaisUtils.Credentials oracle_creds_creds = NaisUtils.getCredentials("oracle_creds");
        setProperty(VEILARBREGISTRERINGDB_USERNAME, oracle_creds_creds.username);
        setProperty(VEILARBREGISTRERINGDB_PASSWORD, oracle_creds_creds.password);

        setProperty(AKTOER_ENDPOINT_URL, getRequiredProperty("AKTOER_V2_ENDPOINTURL"));
        setProperty(ABAC_ENDPOINT_URL_PROPERTY_NAME, getRequiredProperty("ABAC_PDP_ENDPOINT_URL"));
        setProperty(STS_URL_KEY, getRequiredProperty("SECURITYTOKENSERVICE_URL"));

        setProperty(SENSU_BATCHES_PER_SECOND_PROPERTY_NAME, "3");

        ApiApp.runApp(ApplicationConfig.class, args);
    }

}
