package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.log.logger

class ArbeidssokerperiodeAvsluttetProducer {

    fun publiserArbeidssokerperiodeAvsluttet(endretFormidlingsgruppeCommand: EndretFormidlingsgruppeCommand, sisteFormidlingsgruppeperiode: Formidlingsgruppeperiode) {

        logger.info("Ny formidlingsgruppe for person: ${endretFormidlingsgruppeCommand.formidlingsgruppe} - arbeidssøkerperiode avsluttet ${endretFormidlingsgruppeCommand.formidlingsgruppeEndret}. " +
                "Nyeste arbeidssøkerperiode før denne endringen er: $sisteFormidlingsgruppeperiode.")
    }
}