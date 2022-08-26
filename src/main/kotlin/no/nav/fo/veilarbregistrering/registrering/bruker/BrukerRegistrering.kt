package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.registrering.veileder.Veileder
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType

abstract class BrukerRegistrering {
    abstract var manueltRegistrertAv: Veileder?
    abstract fun hentType(): BrukerRegistreringType
    abstract val id: Long

    override fun toString(): String {
        return "BrukerRegistrering(manueltRegistrertAv=$manueltRegistrertAv)"
    }
}