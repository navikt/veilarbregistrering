package no.nav.fo.veilarbregistrering.registrering.publisering

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.bruker.AktorId
import java.time.LocalDateTime

class ArbeidssokerRegistrertInternalEvent(
    val aktorId: AktorId,
    private val besvarelse: Besvarelse,
    val opprettetDato: LocalDateTime
) {
    val brukersSituasjon
        get() = besvarelse.dinSituasjon
    val utdanningSvar
        get() = besvarelse.utdanning
    val utdanningBestattSvar
        get() = besvarelse.utdanningBestatt
    val utdanningGodkjentSvar
        get() = besvarelse.utdanningGodkjent
}