package no.nav.fo.veilarbregistrering.besvarelse;

public class BesvarelseTestdataBuilder {
    public static Besvarelse gyldigBesvarelse() {
        return new Besvarelse()
                .setDinSituasjon(DinSituasjonSvar.JOBB_OVER_2_AAR)
                .setSisteStilling(SisteStillingSvar.HAR_HATT_JOBB)
                .setUtdanning(UtdanningSvar.HOYERE_UTDANNING_5_ELLER_MER)
                .setUtdanningGodkjent(UtdanningGodkjentSvar.JA)
                .setUtdanningBestatt(UtdanningBestattSvar.JA)
                .setHelseHinder(HelseHinderSvar.NEI)
                .setAndreForhold(AndreForholdSvar.NEI);
    }

    public static Besvarelse gyldigSykmeldtSkalTilbakeSammeJobbBesvarelse() {
        return new Besvarelse()
                .setFremtidigSituasjon(FremtidigSituasjonSvar.SAMME_ARBEIDSGIVER)
                .setTilbakeIArbeid(TilbakeIArbeidSvar.JA_FULL_STILLING);
    }

    public static Besvarelse gyldigBesvarelseUtenJobb() {
        return gyldigBesvarelse()
                .setDinSituasjon(DinSituasjonSvar.ALDRI_HATT_JOBB)
                .setSisteStilling(SisteStillingSvar.INGEN_SVAR);
    }
}
