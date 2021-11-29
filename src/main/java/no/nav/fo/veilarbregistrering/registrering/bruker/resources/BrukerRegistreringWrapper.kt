package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistrering

data class BrukerRegistreringWrapper(val registrering: BrukerRegistrering) {
    //@JsonProperty(value = "type", access = JsonProperty.Access.READ_ONLY)
    val type: BrukerRegistreringType = registrering.hentType()

}