package no.nav.fo.veilarbregistrering.arbeidssoker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;

public class ArbeidssokerService {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidssokerService.class);

    private final ArbeidssokerRepository arbeidssokerRepository;

    public ArbeidssokerService(ArbeidssokerRepository arbeidssokerRepository) {
        this.arbeidssokerRepository = arbeidssokerRepository;
    }

    @Transactional
    public void behandle(EndretFormidlingsgruppeCommand endretFormidlingsgruppeCommand) {

        if (!endretFormidlingsgruppeCommand.getFoedselsnummer().isPresent()) {
            LOG.warn(format("Foedselsnummer mangler for EndretFormidlingsgruppeCommand med person_id = %s"),
                    endretFormidlingsgruppeCommand.getPerson_id());
            return;
        }

        arbeidssokerRepository.lagre(endretFormidlingsgruppeCommand);
    }

}
