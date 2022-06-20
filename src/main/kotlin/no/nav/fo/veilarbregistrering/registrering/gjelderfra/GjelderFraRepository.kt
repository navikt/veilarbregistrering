package no.nav.fo.veilarbregistrering.registrering.gjelderfra

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistrering
import java.time.LocalDate

interface GjelderFraRepository {
    fun opprettDatoFor(bruker: Bruker, brukerRegistrering: BrukerRegistrering, dato: LocalDate): GjelderFraDato
    fun hentDatoFor(bruker: Bruker): GjelderFraDato
}
