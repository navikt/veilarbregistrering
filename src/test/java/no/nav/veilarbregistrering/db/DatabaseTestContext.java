package no.nav.veilarbregistrering.db;

import lombok.val;
import no.nav.dialogarena.config.fasit.DbCredentials;
import no.nav.dialogarena.config.fasit.FasitUtils;
import no.nav.dialogarena.config.fasit.TestEnvironment;

import java.util.Optional;

import static no.nav.fo.veilarbregistrering.config.ApplicationConfig.APPLICATION_NAME;
import static no.nav.fo.veilarbregistrering.config.DatabaseConfig.*;

public class DatabaseTestContext {

    public static void setupContext(String miljo) {
        val dbCredential = Optional.ofNullable(miljo)
                .map(TestEnvironment::valueOf)
                .map(testEnvironment -> FasitUtils.getDbCredentials(testEnvironment, APPLICATION_NAME));

        if (dbCredential.isPresent()) {
            setDataSourceProperties(dbCredential.get());
        } else {
            setInMemoryDataSourceProperties();
    }

    }

    public static void setupInMemoryContext() {
        setupContext(null);
    }

    private static void setDataSourceProperties(DbCredentials dbCredentials) {
        System.setProperty(VEILARBREGISTRERINGDB_URL, dbCredentials.url);
        System.setProperty(VEILARBREGISTRERINGDB_USERNAME, dbCredentials.getUsername());
        System.setProperty(VEILARBREGISTRERINGDB_PASSWORD, dbCredentials.getPassword());

    }

    private static void setInMemoryDataSourceProperties() {
        System.setProperty(VEILARBREGISTRERINGDB_URL,
                "jdbc:h2:mem:veilarbregistrering;DB_CLOSE_DELAY=-1;MODE=Oracle");
        System.setProperty(VEILARBREGISTRERINGDB_USERNAME, "sa");
        System.setProperty(VEILARBREGISTRERINGDB_PASSWORD, "password");
    }
}