package no.nav.fo.veilarbregistrering.registrering.scheduler;

import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OverforTilArenaTask {

    private static final Logger LOG = LoggerFactory.getLogger(OverforTilArenaTask.class);

    @Scheduled(cron = "0 */5 * * * *")
    @SchedulerLock(name = "overforTilArena")
    public void sendRegistreringerTilArenaCronJob() {

        LOG.info("Test av Job for å overføre registreringer");

    }

}
