package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.log.loggerFor
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import org.springframework.transaction.annotation.Transactional

open class ReaktiveringBrukerService(
    private val brukerTilstandService: BrukerTilstandService,
    private val reaktiveringRepository: ReaktiveringRepository,
    private val oppfolgingGateway: OppfolgingGateway,
    private val metricsService: MetricsService
) {
    @Transactional
    open fun reaktiverBruker(bruker: Bruker, erVeileder: Boolean) {
        val brukersTilstand = brukerTilstandService.hentBrukersTilstand(bruker)
        if (!brukersTilstand.kanReaktiveres()) {
            throw KanIkkeReaktiveresException("Bruker kan ikke reaktiveres.")
        }
        reaktiveringRepository.lagreReaktiveringForBruker(bruker.aktorId)
        oppfolgingGateway.reaktiverBruker(bruker.gjeldendeFoedselsnummer)
        LOG.info("Reaktivering av bruker med aktørId : {}", bruker.aktorId)
        if (erVeileder) {
            metricsService.registrer(Events.MANUELL_REAKTIVERING_EVENT)
        }
    }

    companion object {
        private val LOG = loggerFor<ReaktiveringBrukerService>()
    }
}