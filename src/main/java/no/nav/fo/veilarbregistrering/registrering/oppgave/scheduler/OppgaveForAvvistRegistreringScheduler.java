package no.nav.fo.veilarbregistrering.registrering.oppgave.scheduler;

import net.javacrumbs.shedlock.core.SchedulerLock;
import no.nav.fo.veilarbregistrering.registrering.oppgave.OppgaveForAvvistRegistreringService;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;

import static no.nav.fo.veilarbregistrering.log.CallId.leggTilCallId;

public class OppgaveForAvvistRegistreringScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(OppgaveForAvvistRegistreringScheduler.class);

    private final OppgaveForAvvistRegistreringService oppgaveForAvvistRegistreringService;
    private final UnleashService unleashService;

    public OppgaveForAvvistRegistreringScheduler(
            OppgaveForAvvistRegistreringService oppgaveForAvvistRegistreringService,
            UnleashService unleashService) {
        this.oppgaveForAvvistRegistreringService = oppgaveForAvvistRegistreringService;
        this.unleashService = unleashService;
    }

    @Scheduled(cron = "0/10 * * * * *")
    @SchedulerLock(name = "opprettOppgave")
    public void opprettOppgaveCronJob() {
        if (!asynkOpprettOppgave()) {
            LOG.info("Asynk opprettelse av oppgave er togglet av");
            return;
        }

        leggTilCallId();

        LOG.info("Asynk opprettelse av oppgave er togglet på");
        oppgaveForAvvistRegistreringService.opprettOppgaveAsynk();

        MDC.clear();
    }

    private boolean asynkOpprettOppgave() {
        return unleashService.isEnabled("veilarbregistrering.asynkOpprettOppgave");
    }
}
