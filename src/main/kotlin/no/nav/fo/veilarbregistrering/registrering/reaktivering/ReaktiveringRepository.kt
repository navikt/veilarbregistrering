package no.nav.fo.veilarbregistrering.registrering.reaktivering

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.registrering.bruker.PopulerFoedselsnummerRepository

interface ReaktiveringRepository : PopulerFoedselsnummerRepository {

    fun lagreReaktiveringForBruker(bruker: Bruker): Long

    fun finnReaktiveringer(aktorId: AktorId): List<Reaktivering>
}