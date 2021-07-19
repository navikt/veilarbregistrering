package no.nav.fo.veilarbregistrering.besvarelse

object StillingTestdataBuilder {
    @JvmStatic
    fun gyldigStilling(): Stilling {
        return Stilling()
            .setStyrk08("12345")
            .setLabel("yrkesbeskrivelse")
            .setKonseptId(1246345L)
    }

    @JvmStatic
    fun ingenYrkesbakgrunn(): Stilling {
        return Stilling("X", -1L, "X")
    }
}
