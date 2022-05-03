package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class ArbeidsforholdMapperV2Test {

    @Test
    fun `skal bruke siste arbeidsavtale fra arbeidsforhold`() {
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

        val arbeidsforholdDto =
                ArbeidsforholdDto(
                        arbeidsgiver,
                        ansettelsesperiode,
                        listOf(arbeidsavtale1, arbeidsavtale2),
                        123456
                )

        val arbeidsforhold = ArbeidsforholdMapperV2.map(arbeidsforholdDto)

        assertEquals(arbeidsavtale2.yrke, arbeidsforhold.styrk)
    }

}