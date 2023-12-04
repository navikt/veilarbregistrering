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
    @Scheduled(fixedDelay = 500, initialDelay = 2_000)
    fun start() {
        if (!leaderElectionClient.isLeader) {
            return
        }

        overfoerArbeidssokerperioder()
    }

    private fun overfoerArbeidssokerperioder() {
        if (!unleashClient.isEnabled(FEATURE_TOGGLE)) {
            logger.info("Arbeidssøkerperioder overføring: Feature toggle er av")
            return
        }

        val arbeidssokerperioder = arbeidssokerperiodeService.hentNesteArbeidssokerperioder(500)

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
    }
}
