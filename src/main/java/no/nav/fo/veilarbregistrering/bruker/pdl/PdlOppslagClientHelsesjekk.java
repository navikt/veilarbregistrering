package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.apiapp.selftest.Helsesjekk;
import no.nav.apiapp.selftest.HelsesjekkMetadata;

import static no.nav.sbl.rest.RestUtils.withClient;

public class PdlOppslagClientHelsesjekk implements Helsesjekk {

    private String baseUrl;

    public PdlOppslagClientHelsesjekk(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void helsesjekk() throws Throwable {
        int status = withClient(c ->
                c.target(baseUrl)
                        .request()
                        .options()
                        .getStatus());
        if (!(status >= 200 && status < 300)) {
            throw new IllegalStateException("HTTP status " + status);
        }
    }

    @Override
    public HelsesjekkMetadata getMetadata() {
        return new HelsesjekkMetadata(
                "PDL",
                baseUrl,
                "Ping av PDL",
                true
        );
    }
}
