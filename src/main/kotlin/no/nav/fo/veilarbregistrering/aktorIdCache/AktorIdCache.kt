package no.nav.fo.veilarbregistrering.aktorIdCache

import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import java.time.LocalDateTime

data class AktorIdCache(
   val foedselsnummer: Foedselsnummer,
   val aktorId: AktorId,
   val opprettetDato: LocalDateTime
)
