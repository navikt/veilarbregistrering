package no.nav.fo.veilarbregistrering.registrering.manuell

import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType

data class ManuellRegistrering (
    val id: Long = -1,
    val registreringId: Long,
    val brukerRegistreringType: BrukerRegistreringType,
    val veilederIdent: String,
    val veilederEnhetId: String,
){
    constructor(
        registreringId: Long,
        brukerRegistreringType: BrukerRegistreringType,
        veilederIdent: String,
        enhetsId: String
    ) :
            this(
                registreringId = registreringId,
                brukerRegistreringType = brukerRegistreringType,
                veilederIdent = veilederIdent,
                veilederEnhetId = enhetsId,
            )

    override fun toString(): String {
        return "ManuellRegistrering(id=$id, registreringId=$registreringId, brukerRegistreringType=$brukerRegistreringType, veilederIdent=$veilederIdent, veilederEnhetId=$veilederEnhetId)"
    }
}