package no.nav.fo.veilarbregistrering.registrering.reaktivering

import io.micrometer.core.instrument.Tag
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.log.loggerFor
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerTilstandService
import no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType
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
            metricsService.registrer(Events.REGISTRERING_TILSTANDSFEIL, Tag.of("type", "KAN_IKKE_REAKTIVERES"))
            throw KanIkkeReaktiveresException("Bruker kan ikke reaktiveres.")
        }
        reaktiveringRepository.lagreReaktiveringForBruker(bruker.aktorId)
        oppfolgingGateway.reaktiverBruker(bruker.gjeldendeFoedselsnummer)
        LOG.info("Reaktivering av bruker med aktørId : {}", bruker.aktorId)
        if (erVeileder) {
            metricsService.registrer(Events.MANUELL_REAKTIVERING_EVENT)
        }
        metricsService.registrer(Events.REGISTRERING_FULLFORING_REGISTRERINGSTYPE, RegistreringType.REAKTIVERING)
    }

    companion object {
        private val LOG = loggerFor<ReaktiveringBrukerService>()
    }
}