package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.brukerdialog.security.oidc.provider.SecurityTokenServiceOidcProvider;
import no.nav.brukerdialog.security.oidc.provider.SecurityTokenServiceOidcProviderConfig;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import static no.nav.brukerdialog.security.oidc.provider.SecurityTokenServiceOidcProviderConfig.STS_OIDC_CONFIGURATION_URL_PROPERTY;
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

    @Inject
    private JdbcTemplate jdbcTemplate;

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {

        SecurityTokenServiceOidcProvider securityTokenServiceOidcProvider = new SecurityTokenServiceOidcProvider(SecurityTokenServiceOidcProviderConfig.builder()
                .discoveryUrl(getRequiredProperty(STS_OIDC_CONFIGURATION_URL_PROPERTY))
                .build());

        apiAppConfigurator
                .validateAzureAdExternalUserTokens()
                .issoLogin()
                .sts()
                .oidcProvider(securityTokenServiceOidcProvider);
    }

    @Transactional
    @Override
    public void startup(ServletContext servletContext) {
        MigrationUtils.createTables(jdbcTemplate);
    }
}
