package no.nav.fo.veilarbregistrering.arbeidssoker.perioder

import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssøkerDomainEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssøkerperiodeAvsluttetEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssøkerperiodeStartetEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.Observer
import no.nav.fo.veilarbregistrering.log.logger

class ArbeidssokerperiodeAvsluttetProducer: Observer {

    override fun update(event: ArbeidssøkerDomainEvent) {
        when (event) {
            is ArbeidssøkerperiodeStartetEvent -> behandle(event)
            is ArbeidssøkerperiodeAvsluttetEvent -> behandle(event)
        }
    }

    fun behandle(arbeidssøkerperiodeStartetEvent: ArbeidssøkerperiodeStartetEvent) {
        logger.info("Starter en ny arbeidssøkerperiode ${arbeidssøkerperiodeStartetEvent.fraOgMedDato}")
    }

    fun behandle(arbeidssøkerperiodeAvsluttetEvent: ArbeidssøkerperiodeAvsluttetEvent) {
        logger.info("Avslutter en eksisterende arbeidssøkerperiode ${arbeidssøkerperiodeAvsluttetEvent.tilOgMedDato}")
    }

}