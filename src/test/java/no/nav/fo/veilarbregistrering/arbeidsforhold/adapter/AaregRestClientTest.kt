package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AaregRestClientTest {

    private val aaregRestClient: AaregRestClient = StubAaregRestClient()

    @Test
    fun `skal parse formidlingshistorikkResponse`() {
        val arbeidsforholdDtos = aaregRestClient.finnArbeidsforhold(Foedselsnummer.of("12345678910"))
        assertThat(arbeidsforholdDtos).hasSize(1)
        val arbeidsforholdDto = ArbeidsforholdDto()
        val arbeidsgiverDto = ArbeidsgiverDto()
        arbeidsgiverDto.organisasjonsnummer = "981129687"
        arbeidsgiverDto.type = "Organisasjon"
        arbeidsforholdDto.arbeidsgiver = arbeidsgiverDto
        val ansettelsesperiodeDto = AnsettelsesperiodeDto()
        val periodeDto = PeriodeDto()
        periodeDto.fom = "2014-07-01"
        periodeDto.tom = "2015-12-31"
        ansettelsesperiodeDto.periode = periodeDto
        arbeidsforholdDto.ansettelsesperiode = ansettelsesperiodeDto
        val arbeidsavtaleDto = ArbeidsavtaleDto()
        arbeidsavtaleDto.yrke = "2130123"
        arbeidsforholdDto.arbeidsavtaler = listOf(arbeidsavtaleDto)
        arbeidsforholdDto.navArbeidsforholdId = 123456
        assertThat(arbeidsforholdDtos).containsOnly(arbeidsforholdDto)
    }
}
