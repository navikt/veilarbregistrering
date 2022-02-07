package no.nav.fo.veilarbregistrering.registrering.formidling

import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstand
import java.time.LocalDateTime

/**
 * RegistreringTilstand representerer tilstanden til en BrukerRegistrering på vei mot Arena.
 * Status sier noe om hvor langt registreringen har kommet ift. overføring til Arena og evt. response derfra.
 */
class RegistreringTilstand private constructor(
    val id: Long,
    val brukerRegistreringId: Long,
    val opprettet: LocalDateTime,
    val sistEndret: LocalDateTime?,
    val status: Status
) {
    fun oppdaterStatus(status: Status): RegistreringTilstand =
        RegistreringTilstand(id, brukerRegistreringId, opprettet, LocalDateTime.now(), status)

    override fun toString(): String =
        "RegistreringTilstand{, id=$id, brukerRegistreringId=$brukerRegistreringId, opprettet=$opprettet, sistEndret=$sistEndret, status=$status}"

    companion object {
        fun of(
            id: Long,
            brukerRegistreringId: Long,
            opprettet: LocalDateTime,
            sistEndret: LocalDateTime?,
            status: Status
        ): RegistreringTilstand = RegistreringTilstand(id, brukerRegistreringId, opprettet, sistEndret, status)

        @JvmStatic
        fun medStatus(status: Status, brukerRegistreringId: Long): RegistreringTilstand =
            RegistreringTilstand(-1L, brukerRegistreringId, LocalDateTime.now(), null, status)
    }
}