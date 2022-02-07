package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.registrering.manuell.Veileder
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistrering

abstract class BrukerRegistrering {
    abstract var manueltRegistrertAv: Veileder?
    abstract fun hentType(): BrukerRegistreringType
    abstract val id: Long

    override fun toString(): String {
        return "BrukerRegistrering(manueltRegistrertAv=$manueltRegistrertAv)"
    }
}