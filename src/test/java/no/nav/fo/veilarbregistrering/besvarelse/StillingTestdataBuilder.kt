package no.nav.fo.veilarbregistrering.besvarelse

object StillingTestdataBuilder {
    @JvmStatic
    fun gyldigStilling(
        styrk08: String = "12345",
        label: String? = "yrkesbeskrivelse",
        konseptId: Long = 1246345L,
    ): Stilling =
        Stilling(styrk08 = styrk08, label = label, konseptId = konseptId)

    @JvmStatic
    fun ingenYrkesbakgrunn(): Stilling {
        return Stilling("X", -1L, "X")
    }
}
