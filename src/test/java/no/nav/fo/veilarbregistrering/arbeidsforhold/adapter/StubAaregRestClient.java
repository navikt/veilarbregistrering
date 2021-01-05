package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter;

import no.nav.fo.veilarbregistrering.FileToJson;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;

class StubAaregRestClient extends AaregRestClient {

    StubAaregRestClient() {
        super(null, null, null);
    }

    @Override
    protected String utforRequest(Foedselsnummer fnr) {
        return FileToJson.toJson("/arbeidsforhold/arbeidsforhold.json");
    }
}
