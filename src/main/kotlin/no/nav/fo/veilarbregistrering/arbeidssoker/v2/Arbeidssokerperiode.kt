package no.nav.fo.veilarbregistrering.arbeidssoker.v2

import java.time.LocalDateTime

data class Arbeidssokerperiode(var fraDato: LocalDateTime, var tilDato: LocalDateTime?) {

    fun avslutt(tilDato: LocalDateTime) {
        if (this.tilDato != null) throw IllegalStateException("Arbeidssokerperiode har allerede en tilDato - kan ikke avslutte en allerede avsluttet periode")
        this.tilDato = tilDato
    }
}