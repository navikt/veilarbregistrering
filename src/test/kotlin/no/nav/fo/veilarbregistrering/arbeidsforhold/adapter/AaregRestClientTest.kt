package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AaregRestClientTest {

    private val aaregRestClient: AaregRestClient = StubAaregRestClient()


    @Test
    fun `skal parse formidlingshistorikkResponse`() {
        val arbeidsforholdDtos = aaregRestClient.finnArbeidsforhold(Foedselsnummer("12345678910"))
        assertThat(arbeidsforholdDtos).hasSize(1)

        val arbeidsgiver = ArbeidsgiverDto(
            organisasjonsnummer = "981129687",
            type = "Organisasjon",
        )

        val ansettelsesperiode = AnsettelsesperiodeDto(
            periode = PeriodeDto(
                fom = "2014-07-01",
                tom = "2015-12-31",
            ),
        )

        val arbeidsavtale = ArbeidsavtaleDto(
            yrke = "2130123",
            gyldighetsperiode = GyldighetsperiodeDto(
                fom = "2014-07-01",
                tom = "2015-12-31"
            )
        )

        val arbeidsforhold =
            ArbeidsforholdDto(
                arbeidsgiver,
                ansettelsesperiode,
                listOf(arbeidsavtale),
                123456
            )

        assertThat(arbeidsforholdDtos.first()).isEqualTo(arbeidsforhold)
    }
}
