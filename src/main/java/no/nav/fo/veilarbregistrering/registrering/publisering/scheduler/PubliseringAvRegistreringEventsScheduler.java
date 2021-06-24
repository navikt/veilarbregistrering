package no.nav.fo.veilarbregistrering.registrering.publisering.scheduler;


import no.nav.common.featuretoggle.UnleashClient;
import no.nav.common.job.leader_election.LeaderElectionClient;
import no.nav.fo.veilarbregistrering.registrering.publisering.PubliseringAvEventsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;

import static no.nav.fo.veilarbregistrering.log.CallId.leggTilCallId;

public class PubliseringAvRegistreringEventsScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(PubliseringAvRegistreringEventsScheduler.class);

    private final PubliseringAvEventsService publiseringAvEventsService;
    private final LeaderElectionClient leaderElectionClient;
    private final UnleashClient unleashClient;

    public PubliseringAvRegistreringEventsScheduler(PubliseringAvEventsService publiseringAvEventsService, LeaderElectionClient leaderElectionClient, UnleashClient unleashClient) {
        this.publiseringAvEventsService = publiseringAvEventsService;
        this.leaderElectionClient = leaderElectionClient;
        this.unleashClient = unleashClient;
    }

    @Scheduled(cron = "0/10 * * * * *")
    public void publiserRegistreringEvents() {
        try {
            leggTilCallId();

            if (unleashClient.isEnabled("veilarbregistrering.publiserRegistreringEvents.toggleOff")) {
                LOG.info("publisering av event er disablet for namespace");
                return;
            }

            if (!leaderElectionClient.isLeader()) {
                return;
            }

            publiseringAvEventsService.publiserEvents();
        } finally {
            MDC.clear();
        }
    }
}
