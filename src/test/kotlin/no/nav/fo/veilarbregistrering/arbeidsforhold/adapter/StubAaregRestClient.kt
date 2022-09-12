package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import no.nav.common.auth.context.AuthContextHolder
import no.nav.fo.veilarbregistrering.FileToJson.toJson
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.tokenveksling.erAADToken
import okhttp3.Request

internal class StubAaregRestClient : AaregRestClient(mockk(relaxed = true), "/test.nav.no", mockAuthContextHolder(), mockk(), { "token" }) {
    override fun utfoerRequest(request: Request) = toJson("/arbeidsforhold/arbeidsforhold.json")
    override fun buildRequestAzureAD(fnr: Foedselsnummer): Request = buildRequest()
    override fun buildRequestTokenX(fnr: Foedselsnummer): Request = buildRequest()

    private fun buildRequest() = Request.Builder().url("https://localhost:9090").build()
}

fun mockAuthContextHolder(): AuthContextHolder {
    val mockAuthContextHolder = mockk<AuthContextHolder>()
    mockkStatic("no.nav.fo.veilarbregistrering.tokenveksling.TokenResolverKt")
    every { mockAuthContextHolder.erAADToken() } returns false
    return mockAuthContextHolder
}