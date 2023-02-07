package no.nav.fo.veilarbregistrering.arbeidssoker

import java.time.LocalDateTime

data class Arbeidssokerperiode(var fraDato: LocalDateTime, var tilDato: LocalDateTime?) {

    fun avslutt(tilDato: LocalDateTime) {
        if (this.tilDato != null)
            throw IllegalStateException("Arbeidssokerperiode har allerede en tilDato - kan ikke avslutte en allerede avsluttet periode")
        if (tilDato.isBefore(this.fraDato))
            throw IllegalStateException("Tildato ($tilDato) kan ikke være før fradato ($fraDato) for en Arbeidssøkerperiode")
        this.tilDato = tilDato
    }
}