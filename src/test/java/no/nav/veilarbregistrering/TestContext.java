package no.nav.veilarbregistrering;

import no.nav.fasit.FasitUtils;
import no.nav.fasit.ServiceUser;
import no.nav.sbl.dialogarena.common.abac.pep.CredentialConstants;
import no.nav.sbl.dialogarena.common.cxf.StsSecurityConstants;

import static java.lang.System.setProperty;
import static no.nav.fasit.FasitUtils.Zone.FSS;
import static no.nav.fasit.FasitUtils.getDefaultEnvironment;
import static no.nav.fasit.FasitUtils.getServiceUser;
import static no.nav.fo.veilarbregistrering.config.ApplicationConfig.APPLICATION_NAME;
import static no.nav.sbl.dialogarena.common.abac.pep.service.AbacServiceConfig.ABAC_ENDPOINT_URL_PROPERTY_NAME;


public class TestContext {

    public static void setup() {
        String securityTokenService = FasitUtils.getBaseUrl("securityTokenService", FSS);
        ServiceUser srvveilarbregistrering = getServiceUser("srvveilarbregistrering", APPLICATION_NAME);

        setProperty("NAIS_APP_NAME", APPLICATION_NAME);
        setProperty("FASIT_ENVIRONMENT_NAME", "q0");

        //sts
        setProperty(StsSecurityConstants.STS_URL_KEY, securityTokenService);
        setProperty(StsSecurityConstants.SYSTEMUSER_USERNAME, srvveilarbregistrering.getUsername());
        setProperty(StsSecurityConstants.SYSTEMUSER_PASSWORD, srvveilarbregistrering.getPassword());

        // Abac
        setProperty(CredentialConstants.SYSTEMUSER_USERNAME, srvveilarbregistrering.getUsername());
        setProperty(CredentialConstants.SYSTEMUSER_PASSWORD, srvveilarbregistrering.getPassword());
        setProperty(ABAC_ENDPOINT_URL_PROPERTY_NAME, "https://wasapp-" + getDefaultEnvironment() + ".adeo.no/asm-pdp/authorize");
    }
}
