package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.Periode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.String.format;

public class ArbeidssokerService {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidssokerService.class);

    private final ArbeidssokerRepository arbeidssokerRepository;
    private final FormidlingsgruppeGateway formidlingsgruppeGateway;

    public ArbeidssokerService(
            ArbeidssokerRepository arbeidssokerRepository,
            FormidlingsgruppeGateway formidlingsgruppeGateway) {
        this.arbeidssokerRepository = arbeidssokerRepository;
        this.formidlingsgruppeGateway = formidlingsgruppeGateway;
    }

    @Transactional
    public void behandle(EndretFormidlingsgruppeCommand endretFormidlingsgruppeCommand) {

        if (!endretFormidlingsgruppeCommand.getFoedselsnummer().isPresent()) {
            LOG.warn(format("Foedselsnummer mangler for EndretFormidlingsgruppeCommand med person_id = %s",
                    endretFormidlingsgruppeCommand.getPerson_id()));
            return;
        }

        arbeidssokerRepository.lagre(endretFormidlingsgruppeCommand);
    }

    public List<Arbeidssokerperiode> hentArbeidssokerperioder(Foedselsnummer foedselsnummer, Periode forespurtPeriode) {
        Arbeidssokerperioder arbeidssokerperioder = arbeidssokerRepository.finnFormidlingsgrupper(foedselsnummer);

        LOG.info(String.format("Fant arbeidssokerperioder i egen database: %s", arbeidssokerperioder));

        if (arbeidssokerperioder.dekkerHele(forespurtPeriode)) {
            LOG.info("Arbeidssokerperiodene dekker hele perioden, og returneres");
            return arbeidssokerperioder.overlapperMed(forespurtPeriode);
        }

        LOG.info("Arbeidssokerperioden dekkes IKKE av perioden - vi forsøker ORDS-tjenesten");

        Arbeidssokerperioder historiskePerioder = formidlingsgruppeGateway.finnArbeissokerperioder(foedselsnummer, forespurtPeriode);

        LOG.info(String.format("Fant arbeidssokerperioder fra ORDS-tjenesten: %s", historiskePerioder));

        return historiskePerioder.overlapperMed(forespurtPeriode);
    }
}
