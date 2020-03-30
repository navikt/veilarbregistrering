package no.nav.fo.veilarbregistrering.config;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import no.nav.fo.veilarbregistrering.db.DataSourceHelsesjekk;
import no.nav.sbl.jdbc.Database;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
@Import(DataSourceHelsesjekk.class)
@EnableTransactionManagement
public class DatabaseConfig {

    public static final String VEILARBREGISTRERINGDB_URL = "VEILARBREGISTRERINGDB_URL";
    public static final String VEILARBREGISTRERINGDB_USERNAME = "VEILARBREGISTRERINGDB_USERNAME";
    public static final String VEILARBREGISTRERINGDB_PASSWORD = "VEILARBREGISTRERINGDB_PASSWORD";

    @Bean
    public static DataSource getDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getRequiredProperty(VEILARBREGISTRERINGDB_URL));
        config.setUsername(getRequiredProperty(VEILARBREGISTRERINGDB_USERNAME));
        config.setPassword(getRequiredProperty(VEILARBREGISTRERINGDB_PASSWORD));
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        return new HikariDataSource(config);
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public Database database(JdbcTemplate jdbcTemplate) {
        return new Database(jdbcTemplate);
    }

}
