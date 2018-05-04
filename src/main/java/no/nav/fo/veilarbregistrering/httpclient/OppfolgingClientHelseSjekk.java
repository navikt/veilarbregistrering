package no.nav.fo.veilarbregistrering.httpclient;

import no.nav.apiapp.selftest.Helsesjekk;
import no.nav.apiapp.selftest.HelsesjekkMetadata;

import javax.inject.Inject;

import static no.nav.fo.veilarbregistrering.httpclient.OppfolgingClient.VEILARBOPPFOLGINGAPI_URL_PROPERTY_NAME;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class OppfolgingClientHelseSjekk implements Helsesjekk {

    @Inject
    private OppfolgingClient oppfolgingClient;

    private String veilarboppfolgingPingUrl = getRequiredProperty(VEILARBOPPFOLGINGAPI_URL_PROPERTY_NAME) + "/ping";

    @Override
    public void helsesjekk() throws Throwable {
        int status = oppfolgingClient.withClient(c ->
                c.target(veilarboppfolgingPingUrl)
                        .request()
                        .get()
                        .getStatus());
        if (!(status >= 200 && status < 300)) {
            throw new IllegalStateException("HTTP status " + status);
        }
    }

    @Override
    public HelsesjekkMetadata getMetadata() {
        return new HelsesjekkMetadata(
                "veilarboppfolging",
                veilarboppfolgingPingUrl,
                "Ping av veilarboppfolging",
                true
        );
    }

}