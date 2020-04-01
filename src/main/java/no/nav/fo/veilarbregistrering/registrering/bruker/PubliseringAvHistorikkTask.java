package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.besvarelse.DinSituasjonSvar;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PubliseringAvHistorikkTask {

    private static final Logger LOG = LoggerFactory.getLogger(PubliseringAvHistorikkTask.class);

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
    }

    public void utfoer() {
        int initPage = 0;
        Pageable initPageRequest = PageRequest.of(initPage, 100);
        Page<ArbeidssokerRegistrertEventDto> registreringer =
                brukerRegistreringRepository.findRegistreringByPage(initPageRequest);

        int totalPages = registreringer.getTotalPages();
        long totalElements = registreringer.getTotalElements();

        LOG.info("Starter publisering av historiske registreringer av totalt {} elementer og {} sider", totalElements, totalPages);

        registreringer.forEach(dto -> {
            arbeidssokerRegistrertProducer.publiserArbeidssokerRegistrert(
                    dto.getAktor_id(),
                    //TODO: Sjekk om alle verdiene vi har i databasen er støttet
                    DinSituasjonSvar.valueOf(dto.getBegrunnelse_for_registrering()),
                    dto.getOpprettet_dato());
        });

        initPage++;

        Pageable nestePageRequest = PageRequest.of(initPage, 100);
        Page<ArbeidssokerRegistrertEventDto> nesteRegistreringer =
                brukerRegistreringRepository.findRegistreringByPage(nestePageRequest);

        //TODO: Denne løkken bør kjøres rekursivt
    }
}
