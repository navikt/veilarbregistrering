package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import io.mockk.mockk
import no.nav.fo.veilarbregistrering.FileToJson.toJson
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import okhttp3.Request

internal class StubAaregRestClient : AaregRestClient(mockk(relaxed = true), "/test.nav.no", mockk()) {
    override fun utfoer(request: Request) = toJson("/arbeidsforhold/arbeidsforhold.json")
    override fun buildRequest(fnr: Foedselsnummer): Request = buildRequest()

    private fun buildRequest() = Request.Builder().url("https://localhost:9090").build()
}