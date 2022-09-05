package no.nav.fo.veilarbregistrering.registrering.publisering.scheduler

import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.job.leader_election.LeaderElectionClient
import no.nav.fo.veilarbregistrering.log.CallId
import no.nav.fo.veilarbregistrering.registrering.publisering.PubliseringAvEventsService
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.scheduling.annotation.Scheduled

class PubliseringAvRegistreringEventsScheduler(
    private val publiseringAvEventsService: PubliseringAvEventsService,
    private val leaderElectionClient: LeaderElectionClient,
    private val unleashClient: UnleashClient
) {
    @Scheduled(cron = "0/10 * * * * *")
    fun publiserRegistreringEvents() {
        try {
            CallId.leggTilCallId()
            if (unleashClient.isEnabled("veilarbregistrering.publiserRegistreringEvents.toggleOff")) {
                LOG.info("publisering av event er disablet for cluster")
                return
            }
            if (!leaderElectionClient.isLeader) {
                return
            }
            publiseringAvEventsService.publiserEvents()
        } finally {
            MDC.clear()
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(PubliseringAvRegistreringEventsScheduler::class.java)
    }
}