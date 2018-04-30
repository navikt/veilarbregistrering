package no.nav.fo.veilarbregistrering.config;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.dialogarena.aktor.AktorConfig;
import no.nav.fo.veilarbregistrering.db.DataSourceHelsesjekk;
import no.nav.fo.veilarbregistrering.db.MigrationUtils;
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
        RemoteFeatureConfig.class,
        PepConfig.class,
        CacheConfig.class,
        AAregServiceWSConfig.class
})
public class ApplicationConfig implements ApiApplication.NaisApiApplication {

    public static final String APPLICATION_NAME = "veilarbregistrering";
    public static final String RUN_WITH_MOCKS = "RUN_WITH_MOCKS";

    @Inject
    private JdbcTemplate jdbcTemplate;

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
        apiAppConfigurator
                //.azureADB2CLogin()
                .sts();
    }

    @Override
    public String getApplicationName() {
        return APPLICATION_NAME;
    }

    @Override
    public Sone getSone() {
        return Sone.FSS;
    }

    @Transactional
    @Override
    public void startup(ServletContext servletContext) {
        MigrationUtils.createTables(jdbcTemplate);
    }
}
