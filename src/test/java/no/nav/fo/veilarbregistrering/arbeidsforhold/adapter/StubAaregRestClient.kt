package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import io.mockk.mockk
import no.nav.fo.veilarbregistrering.FileToJson.toJson
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

internal class StubAaregRestClient : AaregRestClient("/test.nav.no", mockk(), mockk()) {
    override fun utforRequest(fnr: Foedselsnummer) = toJson("/arbeidsforhold/arbeidsforhold.json")
}
