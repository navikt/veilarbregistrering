package no.nav.fo.veilarbregistrering.besvarelse

object BesvarelseTestdataBuilder {
    fun gyldigBesvarelse(
        dinSituasjon: DinSituasjonSvar = DinSituasjonSvar.JOBB_OVER_2_AAR,
        sisteStilling: SisteStillingSvar = SisteStillingSvar.HAR_HATT_JOBB,
        utdanning: UtdanningSvar = UtdanningSvar.HOYERE_UTDANNING_5_ELLER_MER,
        utdanningGodkjent: UtdanningGodkjentSvar = UtdanningGodkjentSvar.JA,
        utdanningBestatt: UtdanningBestattSvar = UtdanningBestattSvar.JA,
        helseHinder: HelseHinderSvar = HelseHinderSvar.NEI,
        andreForhold: AndreForholdSvar? = AndreForholdSvar.NEI,
    ): Besvarelse =
        Besvarelse(
            utdanning, utdanningBestatt, utdanningGodkjent, helseHinder, andreForhold, sisteStilling, dinSituasjon
        )

    fun gyldigSykmeldtSkalTilbakeSammeJobbBesvarelse(
        fremtidigSituasjon: FremtidigSituasjonSvar = FremtidigSituasjonSvar.SAMME_ARBEIDSGIVER,
        tilbakeIArbeid: TilbakeIArbeidSvar = TilbakeIArbeidSvar.JA_FULL_STILLING,
        utdanning: UtdanningSvar = UtdanningSvar.INGEN_UTDANNING,
    ): Besvarelse =
        Besvarelse(
            utdanning = utdanning,
            fremtidigSituasjon = fremtidigSituasjon,
            tilbakeIArbeid = tilbakeIArbeid
        )
}
