package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import java.time.LocalDate

sealed interface ArbeidssøkerDomainEvent {

    fun fødselsnummer(): Foedselsnummer
}

class ArbeidssøkerperiodeStartetEvent(
    private val foedselsnummer: Foedselsnummer,
     val fraOgMedDato: LocalDate
): ArbeidssøkerDomainEvent {

    override fun fødselsnummer(): Foedselsnummer = foedselsnummer
}

class ArbeidssøkerperiodeAvsluttetEvent(
    private val foedselsnummer: Foedselsnummer,
    val tilOgMedDato: LocalDate): ArbeidssøkerDomainEvent {

    override fun fødselsnummer(): Foedselsnummer = foedselsnummer
}