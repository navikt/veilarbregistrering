package no.nav.fo.veilarbregistrering.db;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class MigrationUtils {
    public static void createTables(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();
    }

}
