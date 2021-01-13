import no.nav.apiapp.ApiApp;
import no.nav.fo.veilarbregistrering.config.ApplicationTestConfig;
import no.nav.testconfig.ApiAppTest;
import okhttp3.HttpUrl;
import okhttp3.HttpUrl;

public class MainTest {
    public static final String TEST_PORT = "8810";

    public static void main(String[] args) {
        ApiAppTest.setupTestContext(ApiAppTest.Config.builder()
                .applicationName("veilarbregistrering")
                .environment("q0")
                .build());
        String[] arguments = {TEST_PORT};
        ApiApp.runApp(ApplicationTestConfig.class, arguments);
    }
}


