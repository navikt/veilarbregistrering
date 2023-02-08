package no.nav.fo.veilarbregistrering.arbeidssoker.perioder

import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssøkerDomainEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssøkerperiodeAvsluttetEvent
import no.nav.fo.veilarbregistrering.arbeidssoker.Observer
import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent
import no.nav.fo.veilarbregistrering.log.logger

class ArbeidssokerperiodeAvsluttetProducer: Observer {

    override fun update(event: ArbeidssøkerDomainEvent) {
        when (event) {
            is ArbeidssøkerperiodeAvsluttetEvent -> behandle(event)
            else -> return
        }
    }

    fun behandle(arbeidssøkerperiodeAvsluttetEvent: ArbeidssøkerperiodeAvsluttetEvent) {
        logger.info("En arbeidssøkerperiode ble avslutttet ${arbeidssøkerperiodeAvsluttetEvent.tilOgMedDato}")
    }

    fun publiserArbeidssokerperiodeAvsluttet(
        formidlingsgruppeEndretEvent: FormidlingsgruppeEndretEvent,
        sisteArbeidssokerperiode: Arbeidssokerperiode
    ) {

        logger.info("Ny formidlingsgruppe for person: ${formidlingsgruppeEndretEvent.formidlingsgruppe} - " +
                "arbeidssøkerperiode avsluttet ${formidlingsgruppeEndretEvent.formidlingsgruppeEndret}. " +
                "Nyeste arbeidssøkerperiode før denne endringen er: $sisteArbeidssokerperiode.")
    }

}