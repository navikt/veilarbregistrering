package no.nav.fo.veilarbregistrering.besvarelse

object BesvarelseTestdataBuilder {
    @JvmStatic
    fun gyldigBesvarelse(): Besvarelse {
        return Besvarelse()
            .setDinSituasjon(DinSituasjonSvar.JOBB_OVER_2_AAR)
            .setSisteStilling(SisteStillingSvar.HAR_HATT_JOBB)
            .setUtdanning(UtdanningSvar.HOYERE_UTDANNING_5_ELLER_MER)
            .setUtdanningGodkjent(UtdanningGodkjentSvar.JA)
            .setUtdanningBestatt(UtdanningBestattSvar.JA)
            .setHelseHinder(HelseHinderSvar.NEI)
            .setAndreForhold(AndreForholdSvar.NEI)
    }

    fun gyldigSykmeldtSkalTilbakeSammeJobbBesvarelse(): Besvarelse {
        return Besvarelse()
            .setFremtidigSituasjon(FremtidigSituasjonSvar.SAMME_ARBEIDSGIVER)
            .setTilbakeIArbeid(TilbakeIArbeidSvar.JA_FULL_STILLING)
            .setUtdanning(UtdanningSvar.INGEN_UTDANNING)
    }

    @JvmStatic
    fun gyldigBesvarelseUtenJobb(): Besvarelse {
        return gyldigBesvarelse()
            .setDinSituasjon(DinSituasjonSvar.ALDRI_HATT_JOBB)
            .setSisteStilling(SisteStillingSvar.INGEN_SVAR)
    }
}
