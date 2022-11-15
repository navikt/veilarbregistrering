package no.nav.fo.veilarbregistrering.arbeidssoker.perioder

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent
import no.nav.fo.veilarbregistrering.log.logger

class ArbeidssokerperiodeAvsluttetProducer {

    fun publiserArbeidssokerperiodeAvsluttet(
        formidlingsgruppeEndretEvent: FormidlingsgruppeEndretEvent,
        sisteArbeidssokerperiode: Arbeidssokerperiode
    ) {

        logger.info("Ny formidlingsgruppe for person: ${formidlingsgruppeEndretEvent.formidlingsgruppe} - " +
                "arbeidssøkerperiode avsluttet ${formidlingsgruppeEndretEvent.formidlingsgruppeEndret}. " +
                "Nyeste arbeidssøkerperiode før denne endringen er: $sisteArbeidssokerperiode.")
    }
}