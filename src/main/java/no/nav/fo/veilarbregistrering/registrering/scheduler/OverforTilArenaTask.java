package no.nav.fo.veilarbregistrering.registrering.scheduler;

import net.javacrumbs.shedlock.core.SchedulerLock;
import no.nav.fo.veilarbregistrering.registrering.bruker.ArenaOverforingService;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static no.nav.fo.veilarbregistrering.log.CallId.leggTilCallId;

@Component
public class OverforTilArenaTask {

    private static final Logger LOG = LoggerFactory.getLogger(OverforTilArenaTask.class);

    private final ArenaOverforingService arenaOverforingService;
    private final UnleashService unleashService;

    public OverforTilArenaTask(ArenaOverforingService arenaOverforingService, UnleashService unleashService) {
        this.arenaOverforingService = arenaOverforingService;
        this.unleashService = unleashService;
    }

    @Scheduled(cron = "0/10 * * * * *")
    @SchedulerLock(name = "overforTilArena")
    public void sendRegistreringerTilArenaCronJob() {
        if (!asynkArenaOverforing()) {
            LOG.info("Asynk overføring til Arena er togglet av");
            return;
        }

        leggTilCallId();

        LOG.info("Asynk overføring til Arena er togglet på");
        arenaOverforingService.utforOverforing();

        MDC.clear();
    }

    private boolean asynkArenaOverforing() {
        return unleashService.isEnabled("veilarbregistrering.asynkArenaOverforing");
    }
}
