package no.nav.fo.veilarbregistrering.profilering;

import no.nav.common.leaderelection.LeaderElection;
import no.nav.fo.veilarbregistrering.registrering.bruker.ArbeidssokerProfilertProducer;
import no.nav.fo.veilarbregistrering.registrering.bruker.ArbeidssokerRegistrertEventDto;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.MINUTES;


public class PubliseringAvProfileringHistorikk implements Runnable {


    private static final Logger LOG = LoggerFactory.getLogger(no.nav.fo.veilarbregistrering.profilering.PubliseringAvProfileringHistorikk.class);

    private final ProfileringRepository profileringRepository;
    private final BrukerRegistreringRepository brukerRegistreringRepository;
    private final ArbeidssokerProfilertProducer arbeidssokerProfilertProducer;
    private final UnleashService unleashService;
    private static final int PAGESIZE = 100;

    public PubliseringAvProfileringHistorikk (
            ProfileringRepository profileringRepository,
            BrukerRegistreringRepository brukerRegistreringRepository,
            ArbeidssokerProfilertProducer arbeidssokerProfilertProducer,
            UnleashService unleashService) {

        this.profileringRepository = profileringRepository;
        this.arbeidssokerProfilertProducer = arbeidssokerProfilertProducer;
        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.unleashService = unleashService;

        Executors.newSingleThreadScheduledExecutor()
                .schedule(this, 5, MINUTES);

    }

    @Override
    public void run() {

        if(LeaderElection.isLeader()) {

            Pageable pageable = PageRequest.of(0, PAGESIZE).first();
            while (this.sjekkFeatureErPa()) {
                Page<ArbeidssokerRegistrertEventDto> registreringer = hentRegistreringer(pageable);
                registreringer.forEach(registrering -> {
                    Profilering profilering = profileringRepository.hentProfileringForId(registrering.getBruker_registrering_id());
                    arbeidssokerProfilertProducer.publiserProfilering(registrering.getAktorId(), profilering.getInnsatsgruppe(), registrering.getOpprettetDato());
                });

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

    private boolean sjekkFeatureErPa () {
        return this.unleashService.isEnabled("veilarbregistrering.publiserProfileringsHistorikkTilKafka");
    }

}
