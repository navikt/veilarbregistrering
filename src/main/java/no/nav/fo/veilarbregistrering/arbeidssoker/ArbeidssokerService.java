package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.common.featuretoggle.UnleashService;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Periode;
import no.nav.fo.veilarbregistrering.metrics.Events;
import no.nav.fo.veilarbregistrering.metrics.JaNei;
import no.nav.fo.veilarbregistrering.metrics.Metric;
import no.nav.fo.veilarbregistrering.metrics.InfluxMetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static java.lang.String.format;

public class ArbeidssokerService {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidssokerService.class);

    static final String VEILARBREGISTRERING_FORMIDLINGSGRUPPE_LOCALCACHE = "veilarbregistrering.formidlingsgruppe.localcache";

    private final ArbeidssokerRepository arbeidssokerRepository;
    private final FormidlingsgruppeGateway formidlingsgruppeGateway;
    private final UnleashService unleashService;
    private InfluxMetricsService influxMetricsService;

    public ArbeidssokerService(
            ArbeidssokerRepository arbeidssokerRepository,
            FormidlingsgruppeGateway formidlingsgruppeGateway,
            UnleashService unleashService,
            InfluxMetricsService influxMetricsService) {
        this.arbeidssokerRepository = arbeidssokerRepository;
        this.formidlingsgruppeGateway = formidlingsgruppeGateway;
        this.unleashService = unleashService;
        this.influxMetricsService = influxMetricsService;
    }

    @Transactional
    public void behandle(EndretFormidlingsgruppeCommand endretFormidlingsgruppeCommand) {

        if (!endretFormidlingsgruppeCommand.getFoedselsnummer().isPresent()) {
            LOG.warn(format("Foedselsnummer mangler for EndretFormidlingsgruppeCommand med person_id = %s",
                    endretFormidlingsgruppeCommand.getPersonId()));
            return;
        }

        if (endretFormidlingsgruppeCommand.getFormidlingsgruppeEndret().isBefore(LocalDateTime.parse("2010-01-01T00:00:00"))) {
            LOG.warn(format("Foreldet formidlingsgruppe-endring (%s) lest fra topic: 'gg-arena-formidlinggruppe-v1'  - denne forkastes.",
                    endretFormidlingsgruppeCommand.getFormidlingsgruppeEndret()));
            return;
        }

        arbeidssokerRepository.lagre(endretFormidlingsgruppeCommand);
    }

    public Arbeidssokerperioder hentArbeidssokerperioder(Bruker bruker, Periode forespurtPeriode) {
        Arbeidssokerperioder arbeidssokerperioderLokalt = arbeidssokerRepository.finnFormidlingsgrupper(bruker.alleFoedselsnummer());
        LOG.info(String.format("Fant følgende arbeidssokerperioder i egen database: %s", arbeidssokerperioderLokalt));

        Arbeidssokerperioder arbeidssokerperioderORDS = formidlingsgruppeGateway.finnArbeissokerperioder(bruker.getGjeldendeFoedselsnummer(), forespurtPeriode);
        LOG.info(String.format("Fikk følgende arbeidssokerperioder fra Arena sin ORDS-tjeneste: %s", arbeidssokerperioderORDS));

        boolean dekkerHele = arbeidssokerperioderLokalt.dekkerHele(forespurtPeriode);

        Arbeidssokerperioder overlappendeArbeidssokerperioderLokalt = arbeidssokerperioderLokalt.overlapperMed(forespurtPeriode);

        Arbeidssokerperioder overlappendeHistoriskePerioderORDS = arbeidssokerperioderORDS.overlapperMed(forespurtPeriode);

        boolean lokalErLikOrds = overlappendeArbeidssokerperioderLokalt.equals(overlappendeHistoriskePerioderORDS);
        influxMetricsService.reportTags(Events.HENT_ARBEIDSSOKERPERIODER_KILDER_GIR_SAMME_SVAR, lokalErLikOrds ? JaNei.JA : JaNei.NEI);
        if (!lokalErLikOrds) {
            LOG.warn(String.format("Periodelister fra lokal cache og Arena-ORDS er ikke like\nForespurt periode: %s\nLokalt: %s\nArena-ORDS: %s",
                    forespurtPeriode, overlappendeArbeidssokerperioderLokalt, overlappendeHistoriskePerioderORDS));
        }

        if (dekkerHele && brukLokalCache()) {
            influxMetricsService.reportTags(Events.HENT_ARBEIDSSOKERPERIODER_KILDE, Kilde.LOKAL);
            LOG.info(String.format("Arbeidssokerperiodene fra egen database dekker hele perioden, og returneres: %s", overlappendeArbeidssokerperioderLokalt));
            return overlappendeArbeidssokerperioderLokalt;
        }

        influxMetricsService.reportTags(Events.HENT_ARBEIDSSOKERPERIODER_KILDE, Kilde.ORDS);
        LOG.info(String.format("Returnerer arbeidssokerperioder fra Arena sin ORDS-tjenesten: %s", overlappendeHistoriskePerioderORDS));

        return overlappendeHistoriskePerioderORDS;
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
