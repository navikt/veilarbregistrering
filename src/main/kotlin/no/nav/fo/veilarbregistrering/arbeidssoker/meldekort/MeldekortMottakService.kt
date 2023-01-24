package no.nav.fo.veilarbregistrering.arbeidssoker.meldekort

import no.nav.fo.veilarbregistrering.aktorIdCache.AktorIdCacheService
import no.nav.fo.veilarbregistrering.log.logger
import org.springframework.transaction.annotation.Transactional

open class MeldekortMottakService(
    private val meldekortRepository: MeldekortRepository,
    private val aktorIdCacheService: AktorIdCacheService) {

    @Transactional
    open fun behandleMeldekortEvent(meldekortEvent: MeldekortEvent) {
        logger.info("Mottatt meldekort-event: ${meldekortEvent.eventOpprettet}, ${meldekortEvent.meldekorttype}")
        meldekortRepository.lagre(meldekortEvent)
        aktorIdCacheService.hentAktorIdFraPDLHvisIkkeFinnes(meldekortEvent.fnr, true)
    }
}