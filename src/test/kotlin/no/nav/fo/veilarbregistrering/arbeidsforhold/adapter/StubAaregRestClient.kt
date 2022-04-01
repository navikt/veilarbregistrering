package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import no.nav.common.auth.context.AuthContextHolder
import no.nav.fo.veilarbregistrering.FileToJson.toJson
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

internal class StubAaregRestClient : AaregRestClient(mockk(relaxed = true), mockk(relaxed = true), "/test.nav.no",
                        "/test.nav.no", mockk(), mockAuthContextHolder(), { "token" }) {
    override fun utforRequest(fnr: Foedselsnummer) = toJson("/arbeidsforhold/arbeidsforhold.json")
    override fun utfoerRequestAad(fnr: Foedselsnummer) = toJson("/arbeidsforhold/arbeidsforhold.json")
}

fun mockAuthContextHolder(): AuthContextHolder {
    val mockAuthContextHolder = mockk<AuthContextHolder>()
    mockkStatic("no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.AaregRestClientKt")
    every{mockAuthContextHolder.hentIssuer()} returns ""
    return mockAuthContextHolder
}