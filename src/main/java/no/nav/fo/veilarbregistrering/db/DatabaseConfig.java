package no.nav.fo.veilarbregistrering.db;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


@Configuration
@EnableTransactionManagement
@Import(DatabaseMigrator.class)
public class DatabaseConfig {

    public static final String VEILARBREGISTRERINGDB_URL = "VEILARBREGISTRERINGDB_URL";
    public static final String VEILARBREGISTRERINGDB_USERNAME = "VEILARBREGISTRERINGDB_USERNAME";
    public static final String VEILARBREGISTRERINGDB_PASSWORD = "VEILARBREGISTRERINGDB_PASSWORD";

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public DatabaseHelsesjekk databaseHelsesjekk(JdbcTemplate jdbcTemplate) {
        return new DatabaseHelsesjekk(jdbcTemplate);
    }
}
