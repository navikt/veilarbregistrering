package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.common.oidc.auth.OidcAuthenticatorConfig;
import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.AAregServiceWSConfig;
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlOppslagConfig;
import no.nav.fo.veilarbregistrering.bruker.adapter.PersonGatewayConfig;
import no.nav.fo.veilarbregistrering.bruker.aktor.AktorConfig;
import no.nav.fo.veilarbregistrering.db.MigrationUtils;
import no.nav.fo.veilarbregistrering.kafka.KafkaConfig;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayConfig;
import no.nav.fo.veilarbregistrering.oppgave.adapter.OppgaveGatewayConfig;
import no.nav.fo.veilarbregistrering.orgenhet.adapter.OrganisasjonEnhetV2Config;
import no.nav.fo.veilarbregistrering.registrering.scheduler.OverforTilArenaSchedulerConfig;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykemeldingGatewayConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
@Import({
        ServiceBeansConfig.class,
        DatabaseConfig.class,
        KafkaConfig.class,
        AktorConfig.class,
        PepConfig.class,
        OverforTilArenaSchedulerConfig.class,
        OrganisasjonEnhetV2Config.class,
        CacheConfig.class,
        AAregServiceWSConfig.class,
        UnleashConfig.class,
        PersonGatewayConfig.class,
        OppfolgingGatewayConfig.class,
        OppgaveGatewayConfig.class,
        SykemeldingGatewayConfig.class,
        PdlOppslagConfig.class
})
public class ApplicationConfig implements ApiApplication {

    public static final String APPLICATION_NAME = "veilarbregistrering";

    @Bean
    SystemUserTokenProvider systemUserTokenProvider() {
        return new SystemUserTokenProvider(
                getRequiredProperty("SECURITY_TOKEN_SERVICE_OPENID_CONFIGURATION_URL"),
                getRequiredProperty("SRVVEILARBREGISTRERING_USERNAME"),
                getRequiredProperty("SRVVEILARBREGISTRERING_PASSWORD")
        );
    }

    @Inject
    private JdbcTemplate jdbcTemplate;

    private OidcAuthenticatorConfig createOpenAmAuthenticatorConfig() {
        String discoveryUrl = getRequiredProperty("OPENAM_DISCOVERY_URL");
        String clientId = getRequiredProperty("VEILARBLOGIN_OPENAM_CLIENT_ID");
        String refreshUrl = getRequiredProperty("VEILARBLOGIN_OPENAM_REFRESH_URL");

        return new OidcAuthenticatorConfig()
                .withDiscoveryUrl(discoveryUrl)
                .withClientId(clientId)
                .withRefreshUrl(refreshUrl)
                .withRefreshTokenCookieName(REFRESH_TOKEN_COOKIE_NAME)
                .withIdTokenCookieName(ID_TOKEN_COOKIE_NAME)
                .withIdentType(IdentType.InternBruker);
    }

    private OidcAuthenticatorConfig createAzureAdB2CConfig() {
        String discoveryUrl = getRequiredProperty("AAD_B2C_DISCOVERY_URL");
        String clientId = getRequiredProperty("AAD_B2C_CLIENTID_USERNAME");

        return new OidcAuthenticatorConfig()
                .withDiscoveryUrl(discoveryUrl)
                .withClientId(clientId)
                .withIdTokenCookieName(AZUREADB2C_OIDC_COOKIE_NAME_SBS)
                .withIdentType(IdentType.EksternBruker);
    }

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
        apiAppConfigurator
                .addOidcAuthenticator(createOpenAmAuthenticatorConfig())
                .addOidcAuthenticator(createAzureAdB2CConfig())
                .sts();
    }

    @Transactional
    @Override
    public void startup(ServletContext servletContext) {
        MigrationUtils.createTables(jdbcTemplate);
    }
}
