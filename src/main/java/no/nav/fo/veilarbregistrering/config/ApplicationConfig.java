package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.dialogarena.aktor.AktorConfig;
import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.AAregServiceWSConfig;
import no.nav.fo.veilarbregistrering.db.DataSourceHelsesjekk;
import no.nav.fo.veilarbregistrering.db.MigrationUtils;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClientHelseSjekk;
import no.nav.fo.veilarbregistrering.httpclient.SykmeldtInfoClientHelseSjekk;
import no.nav.fo.veilarbregistrering.orgenhet.adapter.OrganisasjonEnhetV2Config;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.servlet.ServletContext;

@Configuration
@Import({
        ServiceBeansConfig.class,
        DatabaseConfig.class,
        DataSourceHelsesjekk.class,
        AktorConfig.class,
        PepConfig.class,
        OrganisasjonEnhetV2Config.class,
        CacheConfig.class,
        AAregServiceWSConfig.class,
        OppfolgingClientHelseSjekk.class,
        SykmeldtInfoClientHelseSjekk.class,
        RemoteFeatureConfig.class
})
public class ApplicationConfig implements ApiApplication {

    public static final String APPLICATION_NAME = "veilarbregistrering";

    @Inject
    private JdbcTemplate jdbcTemplate;

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
        apiAppConfigurator
                .validateAzureAdExternalUserTokens()
                .issoLogin()
                .sts();
    }

    @Transactional
    @Override
    public void startup(ServletContext servletContext) {
        MigrationUtils.createTables(jdbcTemplate);
    }
}
