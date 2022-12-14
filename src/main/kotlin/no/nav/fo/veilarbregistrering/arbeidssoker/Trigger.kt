package no.nav.fo.veilarbregistrering.arbeidssoker

import java.time.LocalDateTime

interface Trigger {

    fun hentFraDato(): LocalDateTime
}