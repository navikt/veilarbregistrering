import no.nav.apiapp.ApiApp;
import no.nav.fo.veilarbregistrering.config.ApplicationConfig;

import static java.lang.System.setProperty;

public class Main {

    public static void main(String... args) throws Exception {

        setProperty("http.nonProxyHosts", "*.155.55.|*.192.168.|*.10.|*.local|*.rtv.gov|*.adeo.no|*.nav.no|*.aetat.no|*.devillo.no|*.oera.no");
        setProperty("http.proxyHost", "webproxy-utvikler.nav.no");
        setProperty("http.proxyPort", "8088");
        setProperty("https.proxyHost", "webproxy-utvikler.nav.no");
        setProperty("https.proxyPort", "8088");


        ApiApp.startApp(ApplicationConfig.class, args);
    }

}