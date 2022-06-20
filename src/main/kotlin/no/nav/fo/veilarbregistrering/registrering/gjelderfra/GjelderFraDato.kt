package no.nav.fo.veilarbregistrering.registrering.gjelderfra

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistrering
import java.time.LocalDate

data class GjelderFraDato(val bruker: Bruker, val brukerRegistrering: BrukerRegistrering, val dato: LocalDate)
