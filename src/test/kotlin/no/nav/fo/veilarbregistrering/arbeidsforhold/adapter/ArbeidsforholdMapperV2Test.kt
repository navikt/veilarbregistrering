package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ArbeidsforholdMapperV2Test {

    @Test
    fun `skal bruke gjeldende arbeidsavtale fra arbeidsforhold`() {
        val arbeidsavtale1 = ArbeidsavtaleDto(
                yrke = "111111",
                gyldighetsperiode = GyldighetsperiodeDto(
                        fom = "2014-07-01",
                        tom = "2015-12-31"
                )
        )
        val arbeidsavtale2 = ArbeidsavtaleDto(
                yrke = "2222222",
                gyldighetsperiode = GyldighetsperiodeDto(
                        fom = "2014-07-01",
                )
        )
        val arbeidsforholdDto = lagArbeidsforholdDto(arbeidsavtale1, arbeidsavtale2)

        val arbeidsforhold = ArbeidsforholdMapperV2.map(arbeidsforholdDto)

        assertEquals(arbeidsavtale2.yrke, arbeidsforhold.styrk)
    }

    @Test
    fun `skal bruke siste arbeidsavtale i arbeidsforhold når det ikke er noen gjeldende arbeidsavtaler`() {
        val arbeidsavtale1 = ArbeidsavtaleDto(
            yrke = "111111",
            gyldighetsperiode = GyldighetsperiodeDto(
                fom = "2014-07-01",
                tom = "2015-12-31"
            )
        )
        val arbeidsavtale2 = ArbeidsavtaleDto(
            yrke = "2222222",
            gyldighetsperiode = GyldighetsperiodeDto(
                fom = "2014-07-01",
                tom = "2017-05-06"
            )
        )
        val arbeidsforholdDto = lagArbeidsforholdDto(arbeidsavtale1, arbeidsavtale2)

        val arbeidsforhold = ArbeidsforholdMapperV2.map(arbeidsforholdDto)

        assertEquals(arbeidsavtale2.yrke, arbeidsforhold.styrk)
    }

    @Test
    fun `skal bruke arbeidsavtale med nyeste fom-dato når det er flere gjeldende avtaler i arbeidsforhold`() {
        val arbeidsavtale1 = ArbeidsavtaleDto(
            yrke = "111111",
            gyldighetsperiode = GyldighetsperiodeDto(
                fom = "2014-07-01",
            )
        )
        val arbeidsavtale2 = ArbeidsavtaleDto(
            yrke = "2222222",
            gyldighetsperiode = GyldighetsperiodeDto(
                fom = "2018-07-01",
            )
        )
        val arbeidsforholdDto = lagArbeidsforholdDto(arbeidsavtale1, arbeidsavtale2)

        val arbeidsforhold = ArbeidsforholdMapperV2.map(arbeidsforholdDto)

        assertEquals(arbeidsavtale2.yrke, arbeidsforhold.styrk)
    }

    private fun lagArbeidsforholdDto(vararg arbeidsavtale: ArbeidsavtaleDto): ArbeidsforholdDto {
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

        return ArbeidsforholdDto(
                arbeidsgiver,
                ansettelsesperiode,
                listOf(*arbeidsavtale),
                123456
            )
    }

}