package no.nav.fo.veilarbregistrering.arbeidssoker.meldekort

import no.nav.fo.veilarbregistrering.log.logger

class MeldekortMottakService {

    fun behandleMeldekortEvent(meldekortEvent: MeldekortEvent) {
        logger.info("Mottatt meldekort-event: ${meldekortEvent.eventOpprettet}, ${meldekortEvent.meldekorttype}")
    }
}