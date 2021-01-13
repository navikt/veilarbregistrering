package no.nav.fo.veilarbregistrering.registrering.publisering.scheduler;

import no.nav.common.featuretoggle.UnleashService;
import no.nav.common.leaderelection.LeaderElectionClient;
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertInternalEvent;
import no.nav.fo.veilarbregistrering.registrering.publisering.ArbeidssokerRegistrertProducer;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PubliseringAvHistorikkTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(PubliseringAvHistorikkTask.class);
    private static final int PAGESIZE = 100;

    private final BrukerRegistreringRepository brukerRegistreringRepository;
    private final ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer;
    private final UnleashService unleashService;
    private final LeaderElectionClient leaderElectionClient;

    public PubliseringAvHistorikkTask(
            BrukerRegistreringRepository brukerRegistreringRepository,
            ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer,
            UnleashService unleashService,
            LeaderElectionClient leaderElectionClient) {

        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.arbeidssokerRegistrertProducer = arbeidssokerRegistrertProducer;
        this.unleashService = unleashService;

        /*
        Kan taes inn ved behov for å kjøre ny batch

        Executors.newSingleThreadScheduledExecutor()
                .schedule(this, 5, MINUTES);
         */
        this.leaderElectionClient = leaderElectionClient;
    }

    @Override
    public void run() {
        LOG.info("Running");

        if(leaderElectionClient.isLeader()) {
            LOG.info("I´am the leader");

            Pageable pageable = PageRequest.of(0, PAGESIZE).first();
            while (this.sjekkFeatureErPa()) {
                Page<ArbeidssokerRegistrertInternalEvent> registreringer = hentRegistreringer(pageable);
                registreringer.forEach(this::publiserPaKafka);

                if (!registreringer.hasNext()) {
                    break;
                }

                pageable = registreringer.nextPageable();
            }
        }

        LOG.info("Stopped");
    }

    private Page<ArbeidssokerRegistrertInternalEvent> hentRegistreringer(Pageable pageable) {
        Page<ArbeidssokerRegistrertInternalEvent> registreringer =
                brukerRegistreringRepository.findRegistreringByPage(pageable);

        int pageNumber = pageable.getPageNumber();
        int totalPages = registreringer.getTotalPages();
        long totalElements = registreringer.getTotalElements();

        LOG.info("Henter side {} av totalt {} -> totalt {} brukerregistreringer", pageNumber, totalPages, totalElements);
        return registreringer;
    }

    private void publiserPaKafka(ArbeidssokerRegistrertInternalEvent event) {
        arbeidssokerRegistrertProducer.publiserArbeidssokerRegistrert(event);
    }

    private boolean sjekkFeatureErPa () {
        return this.unleashService.isEnabled("veilarbregistrering.publiserHistorikkTilKafka");
    }
}
