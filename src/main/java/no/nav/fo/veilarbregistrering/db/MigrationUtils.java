package no.nav.fo.veilarbregistrering.db;

import org.flywaydb.core.Flyway;
import org.springframework.jdbc.core.JdbcTemplate;

public class MigrationUtils {
    public static void createTables(JdbcTemplate jdbcTemplate) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(jdbcTemplate.getDataSource());
        flyway.migrate();
    }

}
