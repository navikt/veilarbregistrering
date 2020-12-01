package no.nav.fo.veilarbregistrering.registrering.publisering.scheduler;

import no.nav.common.leaderelection.LeaderElection;
import no.nav.fo.veilarbregistrering.registrering.publisering.PubliseringAvEventsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;

import static no.nav.fo.veilarbregistrering.log.CallId.leggTilCallId;

public class PubliseringAvRegistreringEventsScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(PubliseringAvRegistreringEventsScheduler.class);

    private final PubliseringAvEventsService publiseringAvEventsService;

    public PubliseringAvRegistreringEventsScheduler(PubliseringAvEventsService publiseringAvEventsService) {
        this.publiseringAvEventsService = publiseringAvEventsService;
    }

    @Scheduled(cron = "0/10 * * * * *")
    public void publiserRegistreringEvents() {
        try {
            leggTilCallId();

            if (!LeaderElection.isLeader()) {
                return;
            }

            publiseringAvEventsService.publiserEvents();
        } finally {
            MDC.clear();
        }
    }
}
