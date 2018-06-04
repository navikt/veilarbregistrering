import no.nav.apiapp.ApiApp;
import no.nav.fo.veilarbregistrering.config.ApplicationConfig;
import no.nav.fo.veilarbregistrering.config.ApplicationTestConfig;
import no.nav.testconfig.ApiAppTest;
import no.nav.veilarbregistrering.TestContext;
import no.nav.veilarbregistrering.db.DatabaseTestContext;

import static java.lang.System.getProperty;

public class MainTest {
    public static final String TEST_PORT = "8810";

    public static void main(String[] args) throws Exception {
        ApiAppTest.setupTestContext();
        DatabaseTestContext.setupContext(getProperty("database"));
        TestContext.setup();
        String arguments[] = {TEST_PORT};
        ApiApp.startApiApp(ApplicationTestConfig.class, arguments);
    }
}
