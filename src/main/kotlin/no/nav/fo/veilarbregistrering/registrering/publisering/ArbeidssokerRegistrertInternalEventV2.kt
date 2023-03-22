package no.nav.fo.veilarbregistrering.registrering.publisering

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.bruker.AktorId
import java.time.LocalDateTime

data class ArbeidssokerRegistrertInternalEventV2(
    val aktorId: AktorId,
    val besvarelse: Besvarelse,
    val opprettetDato: LocalDateTime
)