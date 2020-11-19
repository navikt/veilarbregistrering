package no.nav.fo.veilarbregistrering.registrering.scheduler;

import net.javacrumbs.shedlock.core.SchedulerLock;
import no.nav.fo.veilarbregistrering.registrering.bruker.PubliseringAvEventsService;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static no.nav.fo.veilarbregistrering.log.CallId.leggTilCallId;

@Component
public class PubliseringAvRegistreringEventsScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(PubliseringAvRegistreringEventsScheduler.class);

    private final UnleashService unleashService;
    private final PubliseringAvEventsService publiseringAvEventsService;

    public PubliseringAvRegistreringEventsScheduler(UnleashService unleashService, PubliseringAvEventsService publiseringAvEventsService) {
        this.unleashService = unleashService;
        this.publiseringAvEventsService = publiseringAvEventsService;
    }

    @Scheduled(cron = "0/10 * * * * *")
    @SchedulerLock(name = "eventPublisering")
    public void publiserRegistreringEvents() {

        leggTilCallId();

        if (!skalPublisereEvents()) {
            LOG.info("Publisering av registreringevents er togglet av");
            return;
        }

        LOG.info("Publisering av registreringevents er togglet på");
        publiseringAvEventsService.publiserEvents();

        MDC.clear();
    }

    private boolean skalPublisereEvents() {
        return unleashService.isEnabled("veilarbregistrering.publiserEvents");
    }
}
