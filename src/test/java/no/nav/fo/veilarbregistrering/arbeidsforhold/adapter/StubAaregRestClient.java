package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.common.sts.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.FileToJson;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;

import static org.mockito.Mockito.mock;

class StubAaregRestClient extends AaregRestClient {

    StubAaregRestClient() {
        super("/test.nav.no", mock(SystemUserTokenProvider.class));
    }

    @Override
    protected String utforRequest(Foedselsnummer fnr) {
        return FileToJson.toJson("/arbeidsforhold/arbeidsforhold.json");
    }
}
