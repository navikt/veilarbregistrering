package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.bruker.AktorId

interface ReaktiveringRepository {

    fun lagreReaktiveringForBruker(aktorId: AktorId)

    fun finnReaktiveringer(aktorId: AktorId): List<Reaktivering>
}
