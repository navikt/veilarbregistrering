package no.nav.fo.veilarbregistrering.arbeidsforhold.adapter

object ArbeidsforholdDtoTestdataBuilder {

    fun arbeidsforholdDto(): ArbeidsforholdDto {
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

        return arbeidsforhold
    }
}