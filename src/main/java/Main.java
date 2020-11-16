import no.nav.apiapp.ApiApp;
import no.nav.fo.veilarbregistrering.config.ApplicationConfig;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.System.setProperty;
import static no.nav.fo.veilarbregistrering.db.DatabaseConfig.*;
import static no.nav.metrics.MetricsConfig.SENSU_BATCHES_PER_SECOND_PROPERTY_NAME;
import static no.nav.sbl.dialogarena.common.abac.pep.service.AbacServiceConfig.ABAC_ENDPOINT_URL_PROPERTY_NAME;
import static no.nav.sbl.dialogarena.common.cxf.StsSecurityConstants.STS_URL_KEY;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class Main {
    private static final String SECRETS_PATH = "/var/run/secrets/nais.io/";

    public static void main(String... args) throws Exception {

        setProperty("SRVVEILARBREGISTRERING_USERNAME", getVaultSecret("serviceuser_creds/username"));
        setProperty("SRVVEILARBREGISTRERING_PASSWORD", getVaultSecret("serviceuser_creds/password"));

        setProperty(VEILARBREGISTRERINGDB_USERNAME, getVaultSecret("oracle_creds/username"));
        setProperty(VEILARBREGISTRERINGDB_PASSWORD, getVaultSecret("oracle_creds/password"));
        setProperty(VEILARBREGISTRERINGDB_URL, getVaultSecret("oracle_config/jdbc_url"));

        setProperty(ABAC_ENDPOINT_URL_PROPERTY_NAME, getRequiredProperty("ABAC_PDP_ENDPOINT_URL"));
        setProperty(STS_URL_KEY, getRequiredProperty("SECURITYTOKENSERVICE_URL"));

        setProperty(SENSU_BATCHES_PER_SECOND_PROPERTY_NAME, "3");

        ApiApp.runApp(ApplicationConfig.class, args);
    }

    private static String getVaultSecret(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(SECRETS_PATH, path)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Klarte ikke laste property fra vault for path: %s", path), e);
        }
    }

}
