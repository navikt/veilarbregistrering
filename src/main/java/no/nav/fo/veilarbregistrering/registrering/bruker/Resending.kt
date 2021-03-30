package no.nav.fo.veilarbregistrering.registrering.bruker

import java.time.LocalDateTime

object Resending {
    @JvmStatic
    fun kanResendes(registrering: OrdinaerBrukerRegistrering?): Boolean =
        registrering?.opprettetDato?.isAfter(LocalDateTime.now().minusDays(30)) ?: false

}