package no.nav.fo.veilarbregistrering.registrering.reaktivering

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker

interface ReaktiveringRepository {

    fun lagreReaktiveringForBruker(bruker: Bruker)

    fun finnReaktiveringer(aktorId: AktorId): List<Reaktivering>
}