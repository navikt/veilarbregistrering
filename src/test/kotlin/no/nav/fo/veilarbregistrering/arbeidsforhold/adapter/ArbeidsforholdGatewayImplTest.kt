package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import io.mockk.mockk
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforholdTestdataBuilder
import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.ArbeidsforholdDtoTestdataBuilder.arbeidsforholdDto
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder
import no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder.aremark
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ArbeidsforholdGatewayImplTest {

    private val arbeidsforholdGateway: ArbeidsforholdGateway = ArbeidsforholdGatewayImpl(LocalStubAaregRestClient())

    @Test
    fun `hent arbeidsforhold fra aareg via rest`() {
        val flereArbeidsforhold = arbeidsforholdGateway.hentArbeidsforhold(Foedselsnummer("12345678910"))
        assertThat(flereArbeidsforhold).isEqualTo(FlereArbeidsforholdTestdataBuilder.somJson())
    }

    @Test
    fun `hent arbeidsforhold skal støtte tom liste ved 404`() {
        val flereArbeidsforhold = arbeidsforholdGateway.hentArbeidsforhold(aremark())
        assertThat(flereArbeidsforhold.flereArbeidsforhold.isEmpty()).isTrue
    }
}

internal class LocalStubAaregRestClient : AaregRestClient(mockk(relaxed = true), "/test.nav.no", mockk()) {
    override fun finnArbeidsforhold(fnr: Foedselsnummer): List<ArbeidsforholdDto> {
        return when (fnr) {
            aremark() -> emptyList()
            else -> listOf(arbeidsforholdDto())
        }
    }
}
