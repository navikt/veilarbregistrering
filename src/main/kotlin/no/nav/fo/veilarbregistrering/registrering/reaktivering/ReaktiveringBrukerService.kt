package no.nav.fo.veilarbregistrering.registrering.reaktivering

import no.nav.fo.veilarbregistrering.aktorIdCache.AktorIdCacheService
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.log.loggerFor
import no.nav.fo.veilarbregistrering.log.secureLogger
import no.nav.fo.veilarbregistrering.metrics.Events
import no.nav.fo.veilarbregistrering.metrics.MetricsService
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.registrering.Tilstandsfeil
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerTilstandService
import no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType
import org.springframework.transaction.annotation.Transactional

open class ReaktiveringBrukerService(
    private val brukerTilstandService: BrukerTilstandService,
    private val reaktiveringRepository: ReaktiveringRepository,
    private val oppfolgingGateway: OppfolgingGateway,
    private val metricsService: MetricsService,
    private val aktorIdCacheService: AktorIdCacheService
) {
    @Transactional
    open fun reaktiverBruker(bruker: Bruker, erVeileder: Boolean) {
        val brukersTilstand = brukerTilstandService.hentBrukersTilstand(bruker)
        if (!brukersTilstand.kanReaktiveres()) {
            secureLogger.warn("Bruker, ${bruker.aktorId}, kan ikke reaktiveres fordi utledet registreringstype er ${brukersTilstand.registreringstype}")
            metricsService.registrer(Events.REGISTRERING_TILSTANDSFEIL, Tilstandsfeil.KAN_IKKE_REAKTIVERES)
            throw KanIkkeReaktiveresException(
                "Bruker kan ikke reaktiveres fordi utledet registreringstype er ${brukersTilstand.registreringstype}"
            )
        }
        reaktiveringRepository.lagreReaktiveringForBruker(bruker)

        aktorIdCacheService.settInnAktorIdHvisIkkeFinnes(bruker.gjeldendeFoedselsnummer, bruker.aktorId)

        oppfolgingGateway.reaktiverBruker(bruker.gjeldendeFoedselsnummer)
        LOG.info("Reaktivering av bruker med aktørId : {}", bruker.aktorId)
        if (erVeileder) {
            metricsService.registrer(Events.MANUELL_REAKTIVERING_EVENT)
        }
        metricsService.registrer(Events.REGISTRERING_FULLFORING_REGISTRERINGSTYPE, RegistreringType.REAKTIVERING)
    }

    open fun kanReaktiveres(bruker: Bruker): Boolean {
        val kanReaktiveres = oppfolgingGateway.kanReaktiveres(bruker.gjeldendeFoedselsnummer)
        return kanReaktiveres ?: false
    }

    companion object {
        private val LOG = loggerFor<ReaktiveringBrukerService>()
    }
}