package no.nav.fo.veilarbregistrering.arbeidssoker

import java.time.LocalDateTime

data class Arbeidssokerperiode(var fraDato: LocalDateTime, var tilDato: LocalDateTime?) {

    fun avslutt(tilDato: LocalDateTime) {
        if (this.tilDato != null) throw IllegalStateException("Arbeidssokerperiode har allerede en tilDato - kan ikke avslutte en allerede avsluttet periode")
        this.tilDato = tilDato
    }

    fun korrigerForNegativPeriode() {
        this.tilDato = this.tilDato?.minusDays(1)?:throw IllegalStateException("Arbeidssøkerperiode må ha en verdi for å kunne korrigeres")
    }
}