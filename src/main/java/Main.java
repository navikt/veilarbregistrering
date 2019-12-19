import no.nav.apiapp.ApiApp;
import no.nav.common.utils.NaisUtils;
import no.nav.fo.veilarbregistrering.config.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.System.setProperty;
import static no.nav.dialogarena.aktor.AktorConfig.AKTOER_ENDPOINT_URL;
import static no.nav.sbl.dialogarena.common.abac.pep.CredentialConstants.SYSTEMUSER_PASSWORD;
import static no.nav.sbl.dialogarena.common.abac.pep.CredentialConstants.SYSTEMUSER_USERNAME;
import static no.nav.sbl.dialogarena.common.abac.pep.service.AbacServiceConfig.ABAC_ENDPOINT_URL_PROPERTY_NAME;
import static no.nav.sbl.dialogarena.common.cxf.StsSecurityConstants.STS_URL_KEY;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class Main {


    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) throws Exception {

        NaisUtils.Credentials oracle_creds = NaisUtils.getCredentials("oracle_creds");
        LOG.info("Oracle_creds (true/false): " + Boolean.valueOf(oracle_creds != null));
        System.out.println();
        if (oracle_creds != null) {
            LOG.info("Benytter oracle_creds. -> USERNAME: " + oracle_creds.username);
            setProperty(SYSTEMUSER_USERNAME, oracle_creds.username);
            setProperty(SYSTEMUSER_PASSWORD, oracle_creds.password);
        } else {
            LOG.info("Benytter gammel konfig.");
            setProperty(SYSTEMUSER_USERNAME, getRequiredProperty("SRVVEILARBREGISTRERING_USERNAME"));
            setProperty(SYSTEMUSER_PASSWORD, getRequiredProperty("SRVVEILARBREGISTRERING_PASSWORD"));
        }

        setProperty(AKTOER_ENDPOINT_URL, getRequiredProperty("AKTOER_V2_ENDPOINTURL"));
        setProperty(ABAC_ENDPOINT_URL_PROPERTY_NAME, getRequiredProperty("ABAC_PDP_ENDPOINT_URL"));
        setProperty(STS_URL_KEY, getRequiredProperty("SECURITYTOKENSERVICE_URL"));

        ApiApp.runApp(ApplicationConfig.class, args);
    }

}
