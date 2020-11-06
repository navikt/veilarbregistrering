package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.common.leaderelection.LeaderElection;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.MINUTES;

public class PubliseringAvHistorikkTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(PubliseringAvHistorikkTask.class);
    private static final int PAGESIZE = 100;

    private final BrukerRegistreringRepository brukerRegistreringRepository;
    private final ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer;
    private final UnleashService unleashService;

    public PubliseringAvHistorikkTask(
            BrukerRegistreringRepository brukerRegistreringRepository,
            ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer,
            UnleashService unleashService) {

        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.arbeidssokerRegistrertProducer = arbeidssokerRegistrertProducer;
        this.unleashService = unleashService;

        /*
        Kan taes inn ved behov for å kjøre ny batch
         */
        Executors.newSingleThreadScheduledExecutor()
                .schedule(this, 5, MINUTES);

    }

    @Override
    public void run() {
        LOG.info("Running");

        if(LeaderElection.isLeader()) {
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
