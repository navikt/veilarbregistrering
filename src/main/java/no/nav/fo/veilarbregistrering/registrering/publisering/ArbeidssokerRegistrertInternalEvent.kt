package no.nav.fo.veilarbregistrering.registrering.publisering

import no.nav.fo.veilarbregistrering.besvarelse.*
import no.nav.fo.veilarbregistrering.bruker.AktorId
import java.time.LocalDateTime
import java.util.*

class ArbeidssokerRegistrertInternalEvent(
    val aktorId: AktorId,
    private val besvarelse: Besvarelse,
    val opprettetDato: LocalDateTime
) {
    val brukersSituasjon: Optional<DinSituasjonSvar>
        get() = Optional.ofNullable(besvarelse.dinSituasjon)
    val utdanningSvar: Optional<UtdanningSvar>
        get() = Optional.ofNullable(besvarelse.utdanning)
    val utdanningBestattSvar: Optional<UtdanningBestattSvar>
        get() = Optional.ofNullable(besvarelse.utdanningBestatt)
    val utdanningGodkjentSvar: Optional<UtdanningGodkjentSvar>
        get() = Optional.ofNullable(besvarelse.utdanningGodkjent)
}