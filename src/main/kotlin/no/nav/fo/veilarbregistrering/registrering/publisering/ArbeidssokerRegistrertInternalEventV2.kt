package no.nav.fo.veilarbregistrering.registrering.publisering

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import java.time.LocalDateTime

data class ArbeidssokerRegistrertInternalEventV2(
    val foedselsnummer: Foedselsnummer,
    val aktorId: AktorId,
    val registreringsId: Long,
    val besvarelse: Besvarelse,
    val opprettetDato: LocalDateTime
)