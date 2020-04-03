package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.common.leaderelection.LeaderElection;
import no.nav.fo.veilarbregistrering.besvarelse.DinSituasjonSvar;
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

    private final BrukerRegistreringRepository brukerRegistreringRepository;
    private final OrdinaerBrukerRegistrertProducer ordinaerBrukerRegistrertProducer;
    private final UnleashService unleashService;
    private static final int PAGESIZE = 100;

    public PubliseringAvHistorikkTask(
            BrukerRegistreringRepository brukerRegistreringRepository,
            OrdinaerBrukerRegistrertProducer ordinaerBrukerRegistrertProducer,
            UnleashService unleashService) {

        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.ordinaerBrukerRegistrertProducer = ordinaerBrukerRegistrertProducer;
        this.unleashService = unleashService;

        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this, 5, 5, MINUTES);
    }

    @Override
    public void run() {
        LOG.info("Running");

        if(LeaderElection.isLeader()) {
            LOG.info("I´am the leader");

            Pageable pageable = PageRequest.of(0, PAGESIZE).first();
            while (this.sjekkFeatureErPa()) {
                Page<ArbeidssokerRegistrertEventDto> registreringer = hentRegistreringer(pageable);
                registreringer.forEach(this::publiserPaKafka);

                if (!registreringer.hasNext()) {
                    break;
                }

                pageable = registreringer.nextPageable();
            }
        }
    }

    private Page<ArbeidssokerRegistrertEventDto> hentRegistreringer(Pageable pageable) {
        Page<ArbeidssokerRegistrertEventDto> registreringer =
                brukerRegistreringRepository.findRegistreringByPage(pageable);

        int pageNumber = pageable.getPageNumber();
        int totalPages = registreringer.getTotalPages();
        long totalElements = registreringer.getTotalElements();

        LOG.info("Henter side {} av totalt {} -> totalt {} brukerregistreringer", pageNumber, totalPages, totalElements);
        return registreringer;
    }

    private void publiserPaKafka(ArbeidssokerRegistrertEventDto dto) {
        ordinaerBrukerRegistrertProducer.publiserArbeidssokerRegistrert(
                dto.getAktorId(),
                //TODO: Sjekk om alle verdiene vi har i databasen er støttet
                DinSituasjonSvar.valueOf(dto.getBegrunnelseForRegistrering()),
                dto.getOpprettetDato());
    }

    private boolean sjekkFeatureErPa () {
        return this.unleashService.isEnabled("veilarbregistrering.publiserHistorikkTilKafka");
    }
}
