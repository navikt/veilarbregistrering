package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.ArbeidsforholdDtoTestdataBuilder.arbeidsforholdDto
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AaregRestClientTest {

    private val aaregRestClient: AaregRestClient = StubAaregRestClient()

    @Test
    fun `skal parse formidlingshistorikkResponse`() {
        val arbeidsforholdDtos = aaregRestClient.finnArbeidsforhold(Foedselsnummer("12345678910"))
        assertThat(arbeidsforholdDtos).hasSize(1)
        assertThat(arbeidsforholdDtos.first()).isEqualTo(arbeidsforholdDto())
    }
}
