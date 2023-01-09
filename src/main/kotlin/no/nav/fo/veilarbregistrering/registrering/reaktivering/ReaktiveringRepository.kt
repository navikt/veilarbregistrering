package no.nav.fo.veilarbregistrering.registrering.reaktivering

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer

interface ReaktiveringRepository {

    fun lagreReaktiveringForBruker(bruker: Bruker): Long

    fun finnReaktiveringer(aktorId: AktorId): List<Reaktivering>

    fun finnReaktiveringerForFoedselsnummer(foedselsnummerList: List<Foedselsnummer>): List<Reaktivering>
}