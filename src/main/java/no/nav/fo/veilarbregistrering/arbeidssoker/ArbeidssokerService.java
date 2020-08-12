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
                .finnFormidlingsgrupper(foedselsnummer);
        LOG.info(String.format("Fant følgende arbeidssokerperioder i egen database: %s", arbeidssokerperioder));

        Arbeidssokerperioder historiskePerioder = formidlingsgruppeGateway.finnArbeissokerperioder(foedselsnummer, forespurtPeriode);
        LOG.info(String.format("Fikk følgende arbeidssokerperioder fra Arena sin ORDS-tjeneste: %s", historiskePerioder));

        boolean dekkerHele = arbeidssokerperioder.dekkerHele(forespurtPeriode);

        List<Arbeidssokerperiode> overlappendeArbeidssokerperioder = arbeidssokerperioder.overlapperMed(forespurtPeriode);

        List<Arbeidssokerperiode> overlappendeHistoriskePerioder = historiskePerioder.overlapperMed(forespurtPeriode);

        boolean lokalErLikOrds = overlappendeArbeidssokerperioder.equals(overlappendeHistoriskePerioder);
        Metrics.reportTags(Metrics.Event.HENT_ARBEIDSSOKERPERIODER_KILDER_GIR_SAMME_SVAR, lokalErLikOrds ? Metrics.JaNei.JA : Metrics.JaNei.NEI);
        if (!lokalErLikOrds) {
            LOG.warn(String.format("Periodelister fra lokal cache og Arena-ORDS er ikke like\nForespurt periode: %s\nLokalt: %s\nArena-ORDS: %s",
                    forespurtPeriode, overlappendeArbeidssokerperioder, overlappendeHistoriskePerioder));
        }

        if (dekkerHele && brukLokalCache()) {
            Metrics.reportTags(Metrics.Event.HENT_ARBEIDSSOKERPERIODER_KILDE, Kilde.LOKAL);
            LOG.info(String.format("Arbeidssokerperiodene fra egen database dekker hele perioden, og returneres: %s", overlappendeArbeidssokerperioder));
            return overlappendeArbeidssokerperioder;
        }

        Metrics.reportTags(Metrics.Event.HENT_ARBEIDSSOKERPERIODER_KILDE, Kilde.ORDS);
        LOG.info(String.format("Returnerer arbeidssokerperioder fra Arena sin ORDS-tjenesten: %s", overlappendeHistoriskePerioder));

        return overlappendeHistoriskePerioder;
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
