package no.nav.fo.veilarbregistrering.registrering.bruker

import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.log.loggerFor
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.PrometheusMetricsService
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.registrering.bruker.AlderMetrikker.rapporterAlder
import org.springframework.transaction.annotation.Transactional

open class InaktivBrukerService(
    private val brukerTilstandService: BrukerTilstandService,
    private val reaktiveringRepository: ReaktiveringRepository,
    private val oppfolgingGateway: OppfolgingGateway,
    private val prometheusMetricsService: PrometheusMetricsService
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
            prometheusMetricsService.registrer(Events.MANUELL_REAKTIVERING_EVENT)
        }
        rapporterAlder(prometheusMetricsService, bruker.gjeldendeFoedselsnummer)
    }

    companion object {
        private val LOG = loggerFor<InaktivBrukerService>()
    }
}