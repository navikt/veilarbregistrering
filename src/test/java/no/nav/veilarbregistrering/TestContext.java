package no.nav.veilarbregistrering;

import no.nav.brukerdialog.security.Constants;
import no.nav.brukerdialog.tools.SecurityConstants;
import no.nav.fasit.FasitUtils;
import no.nav.fasit.ServiceUser;
import no.nav.sbl.dialogarena.common.abac.pep.CredentialConstants;
import no.nav.sbl.dialogarena.common.cxf.StsSecurityConstants;
import no.nav.testconfig.util.Util;

import static java.lang.System.setProperty;
import static no.nav.brukerdialog.security.oidc.provider.AzureADB2CConfig.EXTERNAL_USERS_AZUREAD_B2C_DISCOVERY_URL;
import static no.nav.brukerdialog.security.oidc.provider.AzureADB2CConfig.EXTERNAL_USERS_AZUREAD_B2C_EXPECTED_AUDIENCE;
import static no.nav.dialogarena.aktor.AktorConfig.AKTOER_ENDPOINT_URL;
import static no.nav.fasit.FasitUtils.Zone.FSS;
import static no.nav.fasit.FasitUtils.*;
import static no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.AAregServiceWSConfig.AAREG_ENDPOINT_URL;
import static no.nav.fo.veilarbregistrering.bruker.adapter.PersonGatewayConfig.PERSON_API_PROPERTY_NAME;
import static no.nav.fo.veilarbregistrering.config.ApplicationConfig.APPLICATION_NAME;
import static no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig.UNLEASH_API_URL_PROPERTY;
import static no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayConfig.OPPFOLGING_API_PROPERTY_NAME;
import static no.nav.fo.veilarbregistrering.orgenhet.adapter.OrganisasjonEnhetV2Config.NORG2_ORGANISASJONENHET_V2_URL;
import static no.nav.sbl.dialogarena.common.abac.pep.service.AbacServiceConfig.ABAC_ENDPOINT_URL_PROPERTY_NAME;


public class TestContext {

    public static void setup() {
        String securityTokenService = FasitUtils.getBaseUrl("securityTokenService", FSS);
        ServiceUser srvveilarbregistrering = getServiceUser("srvveilarbregistrering", APPLICATION_NAME);

        setProperty("NAIS_APP_NAME", APPLICATION_NAME);

        //sts
        setProperty(StsSecurityConstants.STS_URL_KEY, securityTokenService);
        setProperty(StsSecurityConstants.SYSTEMUSER_USERNAME, srvveilarbregistrering.getUsername());
        setProperty(StsSecurityConstants.SYSTEMUSER_PASSWORD, srvveilarbregistrering.getPassword());

        // Abac
        setProperty(CredentialConstants.SYSTEMUSER_USERNAME, srvveilarbregistrering.getUsername());
        setProperty(CredentialConstants.SYSTEMUSER_PASSWORD, srvveilarbregistrering.getPassword());
        setProperty(ABAC_ENDPOINT_URL_PROPERTY_NAME, "https://wasapp-" + getDefaultEnvironment() + ".adeo.no/asm-pdp/authorize");

        setProperty(AKTOER_ENDPOINT_URL, "https://app-" + getDefaultEnvironment() + ".adeo.no/aktoerid/AktoerService/v2");

        setProperty(AAREG_ENDPOINT_URL, "https://modapp-" + getDefaultEnvironment() + ".adeo.no/aareg-core/ArbeidsforholdService/v3");

        setProperty(OPPFOLGING_API_PROPERTY_NAME, "https://localhost.nav.no:8443/veilarboppfolging/api");

        setProperty(PERSON_API_PROPERTY_NAME, "https://localhost.nav.no:8443/veilarbperson/api");

        setProperty(UNLEASH_API_URL_PROPERTY, "https://unleash.nais.adeo.no/api/");

        setProperty(NORG2_ORGANISASJONENHET_V2_URL, "https://app-" + getDefaultEnvironment() + ".adeo.no/norg2/ws/OrganisasjonEnhet/v2");

        String issoHost = FasitUtils.getBaseUrl("isso-host");
        String issoJWS = FasitUtils.getBaseUrl("isso-jwks");
        String issoISSUER = FasitUtils.getBaseUrl("isso-issuer");
        String issoIsAlive = FasitUtils.getBaseUrl("isso.isalive", FSS);
        ServiceUser isso_rp_user = getServiceUser("isso-rp-user", APPLICATION_NAME);
        String loginUrl = getRestService("veilarblogin.redirect-url", getDefaultEnvironment()).getUrl();

        setProperty(Constants.ISSO_HOST_URL_PROPERTY_NAME, issoHost);
        setProperty(Constants.ISSO_RP_USER_USERNAME_PROPERTY_NAME, isso_rp_user.getUsername());
        setProperty(Constants.ISSO_RP_USER_PASSWORD_PROPERTY_NAME, isso_rp_user.getPassword());
        setProperty(Constants.ISSO_JWKS_URL_PROPERTY_NAME, issoJWS);
        setProperty(Constants.ISSO_ISSUER_URL_PROPERTY_NAME, issoISSUER);
        setProperty(Constants.ISSO_ISALIVE_URL_PROPERTY_NAME, issoIsAlive);
        setProperty(SecurityConstants.SYSTEMUSER_USERNAME, srvveilarbregistrering.getUsername());
        setProperty(SecurityConstants.SYSTEMUSER_PASSWORD, srvveilarbregistrering.getPassword());
        setProperty(Constants.OIDC_REDIRECT_URL_PROPERTY_NAME, loginUrl);

        ServiceUser azureADClientId = getServiceUser("aad_b2c_clientid", APPLICATION_NAME);
        Util.setProperty(EXTERNAL_USERS_AZUREAD_B2C_DISCOVERY_URL, FasitUtils.getBaseUrl("aad_b2c_discovery"));
        Util.setProperty(EXTERNAL_USERS_AZUREAD_B2C_EXPECTED_AUDIENCE, azureADClientId.username);

    }
}
