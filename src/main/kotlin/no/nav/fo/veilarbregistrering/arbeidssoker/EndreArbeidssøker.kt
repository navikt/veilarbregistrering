package no.nav.fo.veilarbregistrering.arbeidssoker

import java.time.LocalDateTime

interface EndreArbeidssøker {
    fun opprettetTidspunkt(): LocalDateTime
}