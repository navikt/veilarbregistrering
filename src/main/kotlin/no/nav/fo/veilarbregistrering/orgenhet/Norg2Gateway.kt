package no.nav.fo.veilarbregistrering.orgenhet

import no.nav.fo.veilarbregistrering.enhet.Kommune
import java.util.*

interface Norg2Gateway {
    fun hentEnhetFor(kommune: Kommune): Enhetnr?
    fun hentAlleEnheter(): Map<Enhetnr, NavEnhet>
}