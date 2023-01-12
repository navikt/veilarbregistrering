package no.nav.fo.veilarbregistrering.aktorIdCache

import java.time.LocalDateTime

data class AktorIdCache(
   val foedselsnummer: String,
   val aktorId: String,
   val opprettetDato: LocalDateTime
)
