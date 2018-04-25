import no.nav.testconfig.ApiAppTest;

public class MainTest {
    public static final String TEST_PORT = "8800";

    public static void main(String[] args) throws Exception {
        ApiAppTest.setupTestContext();
        Main.main(TEST_PORT);
    }
}
