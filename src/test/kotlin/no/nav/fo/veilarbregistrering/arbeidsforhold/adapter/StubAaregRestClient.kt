package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import io.mockk.mockk
import no.nav.fo.veilarbregistrering.FileToJson.toJson
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

internal class StubAaregRestClient : AaregRestClient(mockk(relaxed = true), "/test.nav.no", "/test.nav.no", mockk(), mockk(), { "token" }) {
    override fun utforRequest(fnr: Foedselsnummer) = toJson("/arbeidsforhold/arbeidsforhold.json")
    override fun utfoerRequestAad(fnr: Foedselsnummer) = toJson("/arbeidsforhold/arbeidsforhold.json")
}
