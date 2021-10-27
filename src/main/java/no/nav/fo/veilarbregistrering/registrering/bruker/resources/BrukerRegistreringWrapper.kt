package no.nav.fo.veilarbregistrering.registrering.bruker.resources

import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistrering
import no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType

data class BrukerRegistreringWrapper(val registrering: BrukerRegistrering) {
    //@JsonProperty(value = "type", access = JsonProperty.Access.READ_ONLY)
    val type: BrukerRegistreringType = registrering.hentType()

}