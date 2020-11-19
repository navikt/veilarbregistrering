package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.ServletUtil;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.common.oidc.Constants;
import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.common.oidc.auth.OidcAuthenticatorConfig;
import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.AAregServiceWSConfig;
import no.nav.fo.veilarbregistrering.arbeidssoker.adapter.FormidlingsgruppeGatewayConfig;
import no.nav.fo.veilarbregistrering.arbeidssoker.resources.InternalArbeidssokerServlet;
import no.nav.fo.veilarbregistrering.bruker.adapter.PersonGatewayConfig;
import no.nav.fo.veilarbregistrering.bruker.krr.KrrConfig;
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlOppslagConfig;
import no.nav.fo.veilarbregistrering.bruker.resources.InternalIdentServlet;
import no.nav.fo.veilarbregistrering.db.DatabaseConfig;
import no.nav.fo.veilarbregistrering.db.MigrationUtils;
import no.nav.fo.veilarbregistrering.enhet.adapter.EnhetGatewayConfig;
import no.nav.fo.veilarbregistrering.kafka.KafkaConfig;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayConfig;
import no.nav.fo.veilarbregistrering.oppgave.adapter.OppgaveGatewayConfig;
import no.nav.fo.veilarbregistrering.orgenhet.adapter.Norg2GatewayConfig;
import no.nav.fo.veilarbregistrering.registrering.resources.InternalRegistreringStatusServlet;
import no.nav.fo.veilarbregistrering.registrering.resources.InternalRegistreringStatusoversiktServlet;
import no.nav.fo.veilarbregistrering.registrering.scheduler.OppgaveForAvvistRegistreringSchedulerConfig;
import no.nav.fo.veilarbregistrering.registrering.scheduler.OverforTilArenaSchedulerConfig;
import no.nav.fo.veilarbregistrering.registrering.scheduler.PubliseringAvRegistreringEventsSchedulerConfig;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykemeldingGatewayConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
@Import({
        ServiceBeansConfig.class,
        DatabaseConfig.class,
        KafkaConfig.class,
        PepConfig.class,
        OverforTilArenaSchedulerConfig.class,
        OppgaveForAvvistRegistreringSchedulerConfig.class,
        PubliseringAvRegistreringEventsSchedulerConfig.class,
        Norg2GatewayConfig.class,
        CacheConfig.class,
        AAregServiceWSConfig.class,
        UnleashConfig.class,
        PersonGatewayConfig.class,
        OppfolgingGatewayConfig.class,
        OppgaveGatewayConfig.class,
        SykemeldingGatewayConfig.class,
        PdlOppslagConfig.class,
        EnhetGatewayConfig.class,
        KrrConfig.class,
        FormidlingsgruppeGatewayConfig.class
})
public class ApplicationConfig implements ApiApplication {

    public static final String APPLICATION_NAME = "veilarbregistrering";

    @Bean
    SystemUserTokenProvider systemUserTokenProvider() {
        return new SystemUserTokenProvider(
                getRequiredProperty("SECURITY_TOKEN_SERVICE_DISCOVERY_URL"),
                getRequiredProperty("SRVVEILARBREGISTRERING_USERNAME"),
                getRequiredProperty("SRVVEILARBREGISTRERING_PASSWORD")
        );
    }

    @Bean
    GammelSystemUserTokenProvider gammelSystemUserTokenProvider() {
        return new GammelSystemUserTokenProvider();
    }

    @Inject
    private JdbcTemplate jdbcTemplate;

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
        apiAppConfigurator
                .addOidcAuthenticator(createOpenAmAuthenticatorConfig())
                .addOidcAuthenticator(createAzureAdB2CConfig())
                .addOidcAuthenticator(createSystemUserAuthenticatorConfig())
                .addOidcAuthenticator(createVeilarbloginAADConfig())
                .sts();
    }

    private OidcAuthenticatorConfig createOpenAmAuthenticatorConfig() {
        String discoveryUrl = getRequiredProperty("OPENAM_DISCOVERY_URL");
        String clientId = getRequiredProperty("VEILARBLOGIN_OPENAM_CLIENT_ID");
        String refreshUrl = getRequiredProperty("VEILARBLOGIN_OPENAM_REFRESH_URL");

        return new OidcAuthenticatorConfig()
                .withDiscoveryUrl(discoveryUrl)
                .withClientId(clientId)
                .withRefreshUrl(refreshUrl)
                .withRefreshTokenCookieName(Constants.REFRESH_TOKEN_COOKIE_NAME)
                .withIdTokenCookieName(Constants.OPEN_AM_ID_TOKEN_COOKIE_NAME) //FIXME: Verifiser riktig bruk
                .withIdentType(IdentType.InternBruker);
    }

    private OidcAuthenticatorConfig createVeilarbloginAADConfig() {
        String discoveryUrl = getRequiredProperty("AAD_DISCOVERY_URL");
        String clientId = getRequiredProperty("VEILARBLOGIN_AAD_CLIENT_ID");

        return new OidcAuthenticatorConfig()
                .withDiscoveryUrl(discoveryUrl)
                .withClientId(clientId)
                .withIdTokenCookieName(Constants.AZURE_AD_ID_TOKEN_COOKIE_NAME)
                .withIdentType(IdentType.InternBruker);
    }

    private OidcAuthenticatorConfig createAzureAdB2CConfig() {
        String discoveryUrl = getRequiredProperty("LOGINSERVICE_IDPORTEN_DISCOVERY_URL");
        String clientId = getRequiredProperty("LOGINSERVICE_IDPORTEN_AUDIENCE");

        return new OidcAuthenticatorConfig()
                .withDiscoveryUrl(discoveryUrl)
                .withClientId(clientId)
                .withIdTokenCookieName(Constants.AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME)
                .withIdentType(IdentType.EksternBruker);
    }

    private OidcAuthenticatorConfig createSystemUserAuthenticatorConfig() {
        String discoveryUrl = getRequiredProperty("SECURITY_TOKEN_SERVICE_DISCOVERY_URL");
        String clientId = getRequiredProperty("SECURITY_TOKEN_SERVICE_CLIENT_ID");

        return new OidcAuthenticatorConfig()
                .withDiscoveryUrl(discoveryUrl)
                .withClientId(clientId)
                .withIdentType(IdentType.Systemressurs);
    }

    @Transactional
    @Override
    public void startup(ServletContext servletContext) {
        MigrationUtils.createTables(jdbcTemplate);

        InternalRegistreringStatusoversiktServlet internalRegistreringStatusoversiktServlet = WebApplicationContextUtils.findWebApplicationContext(servletContext).getBean(InternalRegistreringStatusoversiktServlet.class);
        ServletUtil.leggTilServlet(servletContext, internalRegistreringStatusoversiktServlet, InternalRegistreringStatusoversiktServlet.PATH);

        InternalRegistreringStatusServlet internalRegistreringStatusServlet = WebApplicationContextUtils.findWebApplicationContext(servletContext).getBean(InternalRegistreringStatusServlet.class);
        ServletUtil.leggTilServlet(servletContext, internalRegistreringStatusServlet, InternalRegistreringStatusServlet.PATH);

        InternalIdentServlet internalIdentServlet = WebApplicationContextUtils.findWebApplicationContext(servletContext).getBean(InternalIdentServlet.class);
        ServletUtil.leggTilServlet(servletContext, internalIdentServlet, InternalIdentServlet.PATH);

        InternalArbeidssokerServlet internalArbeidssokerServlet = WebApplicationContextUtils.findWebApplicationContext(servletContext).getBean(InternalArbeidssokerServlet.class);
        ServletUtil.leggTilServlet(servletContext, internalArbeidssokerServlet, InternalArbeidssokerServlet.PATH);
    }
}
