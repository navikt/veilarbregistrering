package no.nav.fo.veilarbregistrering.registrering.publisering.scheduler;


import no.nav.common.job.leader_election.LeaderElectionClient;
import no.nav.fo.veilarbregistrering.registrering.publisering.PubliseringAvEventsService;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;

import static no.nav.fo.veilarbregistrering.log.CallId.leggTilCallId;

public class PubliseringAvRegistreringEventsScheduler {

    private final PubliseringAvEventsService publiseringAvEventsService;
    private LeaderElectionClient leaderElectionClient;

    public PubliseringAvRegistreringEventsScheduler(PubliseringAvEventsService publiseringAvEventsService, LeaderElectionClient leaderElectionClient) {
        this.publiseringAvEventsService = publiseringAvEventsService;
        this.leaderElectionClient = leaderElectionClient;
    }

    @Scheduled(cron = "0/10 * * * * *")
    public void publiserRegistreringEvents() {
        try {
            leggTilCallId();

            if (!leaderElectionClient.isLeader()) {
                return;
            }

            publiseringAvEventsService.publiserEvents();
        } finally {
            MDC.clear();
        }
    }
}
