package no.nav.fo.veilarbregistrering.bruker.krr

internal class KrrKontaktinfoDto(
    /**
     * Hvorvidt brukeren ikke har reservert seg for digital post, og han/hun kan varsles digitalt
     * i henhold til eForvaltningsforskriftens §32
     */
    val kanVarsles: Boolean = false,
    val mobiltelefonnummer: String? = null,

    /**
     * Hvorvidt brukeren har reservert seg for digital post
     */
    val reservert: Boolean = false
)