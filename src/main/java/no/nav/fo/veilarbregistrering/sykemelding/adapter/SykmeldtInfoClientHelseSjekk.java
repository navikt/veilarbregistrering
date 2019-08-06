package no.nav.fo.veilarbregistrering.sykemelding.adapter;

import no.nav.apiapp.selftest.Helsesjekk;
import no.nav.apiapp.selftest.HelsesjekkMetadata;

import static no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient.INFOTRYGDAPI_URL_PROPERTY_NAME;
import static no.nav.sbl.rest.RestUtils.withClient;

public class SykmeldtInfoClientHelseSjekk implements Helsesjekk {

    private String infotrygdFOPingUrl = INFOTRYGDAPI_URL_PROPERTY_NAME + "/rest/internal/isAlive";

    @Override
    public void helsesjekk() throws Throwable {
        int status = withClient(c ->
                c.target(infotrygdFOPingUrl)
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
                "infotrygd-fo",
                infotrygdFOPingUrl,
                "Ping av infotrygd-fo",
                true
        );
    }

}