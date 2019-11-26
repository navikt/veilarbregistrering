package no.nav.fo.veilarbregistrering.besvarelse;

public class StillingTestdataBuilder {
    public static Stilling gyldigStilling() {
        return new Stilling()
                .setStyrk08("12345")
                .setLabel("yrkesbeskrivelse")
                .setKonseptId(1246345L);
    }

    public static Stilling ingenYrkesbakgrunn() {
        return new Stilling("X", -1L, "X");
    }
}
