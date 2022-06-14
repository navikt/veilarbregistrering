package no.nav.fo.veilarbregistrering.registrering.reaktivering

import no.nav.fo.veilarbregistrering.bruker.AktorId
import java.time.LocalDateTime

class Reaktivering(val long: Long, val string: AktorId, val opprettetTidspunkt: LocalDateTime)