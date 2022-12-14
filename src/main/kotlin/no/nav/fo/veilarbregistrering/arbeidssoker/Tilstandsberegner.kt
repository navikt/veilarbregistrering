package no.nav.fo.veilarbregistrering.arbeidssoker

import no.nav.fo.veilarbregistrering.registrering.ordinaer.OrdinaerBrukerRegistrering

class Tilstandsberegner {

    fun beregnNyTilstand(trigger: Trigger, eksisterendeTilstand: Tilstand): Tilstand {
        return when(trigger) {
            is OrdinaerBrukerRegistrering -> beregn(trigger, eksisterendeTilstand)
            else -> throw IllegalStateException("Trigger $trigger ikke støttet")
        }
    }

    private fun beregn(trigger: Trigger, eksisterendeTilstand: Tilstand): Tilstand {
        return if (eksisterendeTilstand is AktivArbeidssoker) {
            eksisterendeTilstand
        } else {
            AktivArbeidssoker(fraDato = trigger.hentFraDato())
        }
    }


}