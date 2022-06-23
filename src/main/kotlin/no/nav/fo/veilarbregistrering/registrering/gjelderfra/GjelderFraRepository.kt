package no.nav.fo.veilarbregistrering.registrering.gjelderfra

import no.nav.fo.veilarbregistrering.bruker.Bruker
import java.time.LocalDate

interface GjelderFraRepository {
    fun opprettDatoFor(bruker: Bruker, brukerRegistreringId: Long, dato: LocalDate)
    fun hentDatoFor(bruker: Bruker): GjelderFraDato?
}
