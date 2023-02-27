package no.nav.fo.veilarbregistrering.arbeidssoker.perioder

import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.aktorIdCache.AktorIdCacheRepository
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.PdlOppslagGateway
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.log.secureLogger
import org.springframework.scheduling.annotation.Scheduled

class PopulerHistoriskePerioderScheduler(
    private val populerArbeidssokerperioderService: PopulerArbeidssokerperioderService,
    private val pdlOppslagGateway: PdlOppslagGateway,
    private val aktorIdCacheRepository: AktorIdCacheRepository,
    private val leaderElectionClient: LeaderElectionClient
) {

    @Scheduled(initialDelay = 180000, fixedDelay = Long.MAX_VALUE)
    fun populerHistoriskePerioder() {
        if (!leaderElectionClient.isLeader) {
            return
        }

        logger.info("Starter jobb for å populere arbeidssøkerperioder")
        val tilfeldigeFnr = aktorIdCacheRepository.hentTilfeldigFnr(5)

        tilfeldigeFnr.forEach {
            val identer = pdlOppslagGateway.hentIdenter(fnr = it.foedselsnummer, erSystemKontekst = true)

            val arbeidssoker = populerArbeidssokerperioderService.hentArbeidssøker(
                Bruker(
                    identer.finnGjeldendeFnr(),
                    it.aktorId,
                    identer.finnHistoriskeFoedselsnummer()
                )
            )

            secureLogger.info("Fant følgende perioder for fnr ${identer.finnGjeldendeFnr()}: ${arbeidssoker.allePerioder()}")
        }
    }
}