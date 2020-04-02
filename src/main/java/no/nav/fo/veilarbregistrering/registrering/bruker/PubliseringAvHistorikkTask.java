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

import static java.util.concurrent.TimeUnit.SECONDS;

public class PubliseringAvHistorikkTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(PubliseringAvHistorikkTask.class);

    private final BrukerRegistreringRepository brukerRegistreringRepository;
    private final ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer;
    private final UnleashService unleashService;
    private static final int PAGESIZE = 100;

    public PubliseringAvHistorikkTask(
            BrukerRegistreringRepository brukerRegistreringRepository,
            ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer,
            UnleashService unleashService) {

        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.arbeidssokerRegistrertProducer = arbeidssokerRegistrertProducer;
        this.unleashService = unleashService;

        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this, 60, 30, SECONDS);
    }

    @Override
    public void run() {
        LOG.info("run");
        if(LeaderElection.isLeader()) {
            LOG.info("I´am the leader");
            int currentPage = 0;
            while (this.sjekkFeatureErPa()) {
                Page<ArbeidssokerRegistrertEventDto> registreringer = hentRegistreringer(currentPage);
                if (currentPage > registreringer.getTotalElements()) {
                    break;
                }
                registreringer.forEach(this::publiserPaKafka);
                currentPage += PAGESIZE;
            }
        }
    }

    private Page<ArbeidssokerRegistrertEventDto> hentRegistreringer(int initPage) {

        Pageable initPageRequest = PageRequest.of(initPage, PAGESIZE);
        Page<ArbeidssokerRegistrertEventDto> registreringer =
                brukerRegistreringRepository.findRegistreringByPage(initPageRequest);

        int totalPages = registreringer.getTotalPages();
        long totalElements = registreringer.getTotalElements();

        LOG.info("Henter side {} av totalt {} -> totalt {} brukerregistreringer", initPage, totalPages, totalElements);
        return registreringer;
    }

    private void publiserPaKafka(ArbeidssokerRegistrertEventDto dto) {
        arbeidssokerRegistrertProducer.publiserArbeidssokerRegistrert(
                dto.getAktor_id(),
                //TODO: Sjekk om alle verdiene vi har i databasen er støttet
                DinSituasjonSvar.valueOf(dto.getBegrunnelse_for_registrering()),
                dto.getOpprettet_dato());
    }

    private boolean sjekkFeatureErPa () {
        return this.unleashService.isEnabled("veilarbregistrering.publiserHistorikkTilKafka");
    }
}
