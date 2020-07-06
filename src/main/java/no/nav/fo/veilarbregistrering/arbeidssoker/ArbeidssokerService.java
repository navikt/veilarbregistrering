package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.Periode;
import no.nav.fo.veilarbregistrering.metrics.Metric;
import no.nav.fo.veilarbregistrering.metrics.Metrics;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.String.format;

public class ArbeidssokerService {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidssokerService.class);

    static final String VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE = "veilarbregistrering.formidlingsgruppe.localcache";

    private final ArbeidssokerRepository arbeidssokerRepository;
    private final FormidlingsgruppeGateway formidlingsgruppeGateway;
    private final UnleashService unleashService;

    public ArbeidssokerService(
            ArbeidssokerRepository arbeidssokerRepository,
            FormidlingsgruppeGateway formidlingsgruppeGateway, UnleashService unleashService) {
        this.arbeidssokerRepository = arbeidssokerRepository;
        this.formidlingsgruppeGateway = formidlingsgruppeGateway;
        this.unleashService = unleashService;
    }

    @Transactional
    public void behandle(EndretFormidlingsgruppeCommand endretFormidlingsgruppeCommand) {

        if (!endretFormidlingsgruppeCommand.getFoedselsnummer().isPresent()) {
            LOG.warn(format("Foedselsnummer mangler for EndretFormidlingsgruppeCommand med person_id = %s",
                    endretFormidlingsgruppeCommand.getPersonId()));
            return;
        }

        arbeidssokerRepository.lagre(endretFormidlingsgruppeCommand);
    }

    public List<Arbeidssokerperiode> hentArbeidssokerperioder(Foedselsnummer foedselsnummer, Periode forespurtPeriode) {
        Arbeidssokerperioder arbeidssokerperioder = arbeidssokerRepository
                .finnFormidlingsgrupper(foedselsnummer)
                .sorterOgPopulerTilDato();

        LOG.info(String.format("Fant arbeidssokerperioder i egen database: %s", arbeidssokerperioder));

        boolean dekkerHele = arbeidssokerperioder.dekkerHele(forespurtPeriode);

        Metrics.reportTags(Metrics.Event.HENT_ARBEIDSSOKERPERIODER_POTENSIELLKILDE, dekkerHele ? Kilde.LOKAL : Kilde.ORDS);

        if (dekkerHele && brukLokalCache()) {
            Metrics.reportTags(Metrics.Event.HENT_ARBEIDSSOKERPERIODER_KILDE, Kilde.LOKAL);
            LOG.info("Arbeidssokerperiodene dekker hele perioden, og returneres");
            return arbeidssokerperioder.overlapperMed(forespurtPeriode);
        } else if (dekkerHele) {
            LOG.info("Arbeidssokerperiodene dekker hele perioden");
        }

        LOG.info("Arbeidssokerperioden dekkes IKKE av perioden - vi forsøker ORDS-tjenesten");

        Arbeidssokerperioder historiskePerioder = formidlingsgruppeGateway.finnArbeissokerperioder(foedselsnummer, forespurtPeriode);

        Metrics.reportTags(Metrics.Event.HENT_ARBEIDSSOKERPERIODER_KILDE, Kilde.ORDS);

        LOG.info(String.format("Fant arbeidssokerperioder fra ORDS-tjenesten: %s", historiskePerioder));

        return historiskePerioder.overlapperMed(forespurtPeriode);
    }

    private boolean brukLokalCache() {
        return unleashService.isEnabled(VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE);
    }

    private enum Kilde implements Metric {
        ORDS, LOKAL;

        @Override
        public String fieldName() {
            return "kilde";
        }

        @Override
        public Object value() {
            return this.toString();
        }
    }
}
