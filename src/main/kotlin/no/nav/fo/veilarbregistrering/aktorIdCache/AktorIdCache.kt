package no.nav.fo.veilarbregistrering.aktorIdCache

import java.time.LocalDateTime

data class AktorIdCache(
   val foedselsnummer: String,
   val aktor_id: String,
   val opprettet_dato: LocalDateTime
)
