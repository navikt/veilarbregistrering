package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.arbeidssoker.formidlingsgruppe.FormidlingsgruppeEndretEvent
import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistrering

class Tilstandsberegner {

    fun beregnNyTilstand(trigger: Trigger, eksisterendeTilstand: Tilstand): Tilstand {
        return when(trigger) {
            is OrdinaerBrukerRegistrering -> eksisterendeTilstand.håndter(trigger)
            is FormidlingsgruppeEndretEvent -> eksisterendeTilstand.håndter(trigger)
            else -> throw IllegalStateException("Trigger $trigger ikke støttet")
        }
    }
}