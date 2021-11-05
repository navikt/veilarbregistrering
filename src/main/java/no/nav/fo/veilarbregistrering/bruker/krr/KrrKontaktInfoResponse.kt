package no.nav.fo.veilarbregistrering.bruker.krr

internal data class KrrKontaktInfoResponse(val kontaktinfo: Map<String, KrrKontaktInfo>?, val feil: Map<String, KrrFeilDto>?)

internal data class KrrKontaktInfo(
    /**
     * Hvorvidt brukeren ikke har reservert seg for digital post, og han/hun kan varsles digitalt
     * i henhold til eForvaltningsforskriftens §32
     */
    val kanVarsles: Boolean,
    val mobiltelefonnummer: String?,
    /* Hvorvidt brukeren har reservert seg for digital post */
    val reservert: Boolean
    )

internal data class KrrFeilDto(val melding: String)
