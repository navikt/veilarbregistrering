package no.nav.fo.veilarbregistrering.registrering.veileder

import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet

/**
 * Dette objektet inngår som en del av APIet/kontrakten når vi henter regisitreringer som har blitt registrert av en veileder.
 */
data class Veileder(var ident: String? = null, var enhet: NavEnhet? = null) {
    override fun toString(): String = "Veileder(ident=$ident, enhet=$enhet)"
}