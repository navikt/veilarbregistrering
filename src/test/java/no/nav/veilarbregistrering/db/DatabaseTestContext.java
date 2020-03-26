package no.nav.veilarbregistrering.db;

import static no.nav.fo.veilarbregistrering.config.DatabaseConfig.*;

public class DatabaseTestContext {

    public static void setupInMemoryDatabaseContext() {
        setInMemoryDataSourceProperties();
    }

    private static void setInMemoryDataSourceProperties() {
        System.setProperty(VEILARBREGISTRERINGDB_URL,
                "jdbc:h2:mem:veilarbregistrering;DB_CLOSE_DELAY=-1;MODE=Oracle");
        System.setProperty(VEILARBREGISTRERINGDB_USERNAME, "sa");
        System.setProperty(VEILARBREGISTRERINGDB_PASSWORD, "password");
    }
}