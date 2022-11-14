package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEvent
import no.nav.fo.veilarbregistrering.log.logger

class ArbeidssokerperiodeAvsluttetProducer {

    fun publiserArbeidssokerperiodeAvsluttet(
        formidlingsgruppeEvent: FormidlingsgruppeEvent,
        sisteArbeidssokerperiode: Arbeidssokerperiode) {

        logger.info("Ny formidlingsgruppe for person: ${formidlingsgruppeEvent.formidlingsgruppe} - " +
                "arbeidssøkerperiode avsluttet ${formidlingsgruppeEvent.formidlingsgruppeEndret}. " +
                "Nyeste arbeidssøkerperiode før denne endringen er: $sisteArbeidssokerperiode.")
    }
}