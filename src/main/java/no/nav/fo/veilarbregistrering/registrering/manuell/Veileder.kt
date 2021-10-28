package no.nav.fo.veilarbregistrering.registrering.manuell

import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet

data class Veileder(
    var ident: String? = null,
    var enhet: NavEnhet? = null
) {
    override fun toString(): String = "Veileder(ident=$ident, enhet=$enhet)"
}