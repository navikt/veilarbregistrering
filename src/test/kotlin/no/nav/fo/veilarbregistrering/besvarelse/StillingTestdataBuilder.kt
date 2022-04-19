package no.nav.fo.veilarbregistrering.besvarelse

object StillingTestdataBuilder {
    fun gyldigStilling(
        styrk08: String = "12345",
        label: String? = "yrkesbeskrivelse",
        konseptId: Long = 1246345L,
    ): Stilling =
        Stilling(styrk08 = styrk08, label = label, konseptId = konseptId)

}
