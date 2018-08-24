import no.nav.apiapp.ApiApp;
import no.nav.fo.veilarbregistrering.config.ApplicationConfig;

import static java.lang.System.setProperty;
import static no.nav.brukerdialog.security.Constants.OIDC_REDIRECT_URL_PROPERTY_NAME;
import static no.nav.dialogarena.aktor.AktorConfig.AKTOER_ENDPOINT_URL;
import static no.nav.sbl.dialogarena.common.abac.pep.CredentialConstants.SYSTEMUSER_PASSWORD;
import static no.nav.sbl.dialogarena.common.abac.pep.CredentialConstants.SYSTEMUSER_USERNAME;
import static no.nav.sbl.dialogarena.common.abac.pep.service.AbacServiceConfig.ABAC_ENDPOINT_URL_PROPERTY_NAME;
import static no.nav.sbl.dialogarena.common.cxf.StsSecurityConstants.STS_URL_KEY;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class Main {

    public static void main(String... args) throws Exception {

        setProperty(SYSTEMUSER_USERNAME, getRequiredProperty("SRVVEILARBREGISTRERING_USERNAME"));
        setProperty(SYSTEMUSER_PASSWORD, getRequiredProperty("SRVVEILARBREGISTRERING_PASSWORD"));

        setProperty(AKTOER_ENDPOINT_URL, getRequiredProperty("AKTOER_V2_ENDPOINTURL"));
        setProperty(OIDC_REDIRECT_URL_PROPERTY_NAME, getRequiredProperty("VEILARBLOGIN_REDIRECT_URL_URL"));
        setProperty(ABAC_ENDPOINT_URL_PROPERTY_NAME, getRequiredProperty("ABAC_PDP_ENDPOINT_URL"));
        setProperty(STS_URL_KEY, getRequiredProperty("SECURITYTOKENSERVICE_URL"));


        ApiApp.startApiApp(ApplicationConfig.class, args);
    }

}
