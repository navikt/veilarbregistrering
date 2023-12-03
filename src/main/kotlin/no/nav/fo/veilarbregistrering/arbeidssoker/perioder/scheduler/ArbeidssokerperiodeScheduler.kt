package no.nav.fo.veilarbregistrering.arbeidssoker.perioder.scheduler

import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeService
import no.nav.fo.veilarbregistrering.log.logger
import org.springframework.scheduling.annotation.Scheduled

class ArbeidssokerperiodeScheduler(
    private val leaderElectionClient: LeaderElectionClient,
    private val arbeidssokerperiodeService: ArbeidssokerperiodeService,
    private val arbeidssokerperiodeProducer: ArbeidssokerperiodeProducer,
) {
    @Scheduled(cron = HVERT_TIENDE_SEKUND)
    fun start() {
        if (!leaderElectionClient.isLeader) {
            return
        }
        logger.info("Starter jobb for å overføre arbeidssøkerperioder")

        val arbeidssokerperioder = arbeidssokerperiodeService.hentNesteArbeidssokerperioder()

        if (arbeidssokerperioder.isEmpty()) {
            logger.info("Fant ingen arbeidssøkerperioder som skal overføres")
            return
        }

        val startHendelser = arbeidssokerperioder
            .map {
                ArbeidssokerperiodeHendelseMelding(Hendelse.STARTET, it.foedselsnummer.foedselsnummer, it.fra)
            }

        val stoppHendelser = arbeidssokerperioder
            .filter { it.til !== null }
            .map {
                ArbeidssokerperiodeHendelseMelding(Hendelse.STOPPET, it.foedselsnummer.foedselsnummer, it.til!!)
            }

        val hendelser = (startHendelser + stoppHendelser).sortedBy { it.tidspunkt }

        logger.info("Hendelser til overføring ${hendelser.size}")

        hendelser.forEach { arbeidssokerperiodeProducer.publiserArbeidssokerperioder(it) }

        arbeidssokerperiodeService.settArbeidssokerperioderSomOverfort(arbeidssokerperioder.map { it.id })
    }

    companion object {
        const val HVERT_TIENDE_SEKUND = "0/10 * * * * *"
    }
}
