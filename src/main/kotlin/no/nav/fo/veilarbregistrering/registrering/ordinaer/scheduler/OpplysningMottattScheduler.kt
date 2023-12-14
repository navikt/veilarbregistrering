package no.nav.fo.veilarbregistrering.registrering.ordinaer.scheduler

import io.getunleash.Unleash
import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.log.logger
import no.nav.fo.veilarbregistrering.registrering.ordinaer.BrukerRegistreringService
import org.springframework.scheduling.annotation.Scheduled

class OpplysningMottattScheduler(
    private val leaderElectionClient: LeaderElectionClient,
    private val brukerRegistreringService: BrukerRegistreringService,
    private val opplysningerMottattProducer: OpplysningerMottattProducer,
    private val unleashClient: Unleash,
) {
    @Scheduled(fixedDelay = 60_000, initialDelay = 10_000)
    fun start() {
        if (!leaderElectionClient.isLeader) {
            return
        }

        overfoerOpplysningerMottatt()
    }

    private fun overfoerOpplysningerMottatt() {
        if (!unleashClient.isEnabled(FEATURE_TOGGLE)) {
            logger.info("Opplysninger om arbeidssøker: Feature toggle er av")
            return
        }

        val opplysningerOmArbeidssoekere = brukerRegistreringService.hentNesteOpplysningerOmArbeidssoker(100)

        if (opplysningerOmArbeidssoekere.isEmpty()) {
            logger.info("Opplysninger om arbeidssøker: Fant ingen arbeidssøkeropplysninger som skal overføres")
            return
        }

        logger.info("Opplysninger om arbeidssøker: Hendelser til overføring ${opplysningerOmArbeidssoekere.size}")

        opplysningerOmArbeidssoekere.forEach { (_, opplysninger) -> opplysningerMottattProducer.publiserOpplysningerMottatt(opplysninger) }

        brukerRegistreringService.settOpplysningerOmArbeidssoekerSomOverfort(opplysningerOmArbeidssoekere.map { it.first.toInt() })

        logger.info("Opplysninger om arbeidssøker: Hendelser overført ${opplysningerOmArbeidssoekere.size}")
    }

    companion object {
        const val FEATURE_TOGGLE = "veilarbregistrering.overfoer.opplysningeromarbeidssoeker"
    }
}