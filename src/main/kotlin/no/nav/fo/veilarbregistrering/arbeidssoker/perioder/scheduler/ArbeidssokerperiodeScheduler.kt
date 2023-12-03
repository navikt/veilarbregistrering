package no.nav.fo.veilarbregistrering.arbeidssoker.perioder.scheduler

import io.getunleash.Unleash
import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.arbeidssoker.ArbeidssokerperiodeService
import no.nav.fo.veilarbregistrering.log.logger
import org.springframework.scheduling.annotation.Scheduled

class ArbeidssokerperiodeScheduler(
    private val leaderElectionClient: LeaderElectionClient,
    private val arbeidssokerperiodeService: ArbeidssokerperiodeService,
    private val arbeidssokerperiodeProducer: ArbeidssokerperiodeProducer,
    private val unleashClient: Unleash,
) {
    // @Scheduled(initialDelay = 1000 * 30, fixedDelay = DEAD_MANS_SOLUTION)
    fun start() {
        if (!leaderElectionClient.isLeader) {
            return
        }

        logger.info("Arbeidssøkerperioder overføring: starter overføring av arbeidssøkerperioder")

        while (true) {
            overfoerArbeidssokerperioder()
        }
    }

    private fun overfoerArbeidssokerperioder() {
        if (!unleashClient.isEnabled(FEATURE_TOGGLE)) {
            logger.info("Arbeidssøkerperioder overføring: Feature toggle er av")
            return
        }

        val arbeidssokerperioder = arbeidssokerperiodeService.hentNesteArbeidssokerperioder(50)

        if (arbeidssokerperioder.isEmpty()) {
            logger.info("Arbeidssøkerperioder overføring: Fant ingen arbeidssøkerperioder som skal overføres")
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

        logger.info("Arbeidssøkerperioder overføring: Hendelser til overføring ${hendelser.size}")

        hendelser.forEach { arbeidssokerperiodeProducer.publiserArbeidssokerperioder(it) }

        arbeidssokerperiodeService.settArbeidssokerperioderSomOverfort(arbeidssokerperioder.map { it.id })

        logger.info("Arbeidssøkerperioder overføring: Hendelser overført ${hendelser.size}")
    }

    companion object {
        const val FEATURE_TOGGLE = "veilarbregistrering.overfoer.arbeidssokerperioder"
        const val DEAD_MANS_SOLUTION = Long.MAX_VALUE
    }
}
