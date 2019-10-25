package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import no.nav.apiapp.selftest.Helsesjekk;
import no.nav.apiapp.selftest.HelsesjekkMetadata;

import static no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayConfig.OPPFOLGING_API_PROPERTY_NAME;
import static no.nav.sbl.rest.RestUtils.withClient;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class OppfolgingClientHelseSjekk implements Helsesjekk {

    private String veilarboppfolgingPingUrl = getRequiredProperty(OPPFOLGING_API_PROPERTY_NAME) + "/ping";

    @Override
    public void helsesjekk() throws Throwable {
        int status = withClient(c ->
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