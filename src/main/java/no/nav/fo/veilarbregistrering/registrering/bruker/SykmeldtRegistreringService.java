package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.metrics.InfluxMetricsService;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistrering;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Optional.ofNullable;
import static no.nav.fo.veilarbregistrering.metrics.Events.MANUELL_REGISTRERING_EVENT;
import static no.nav.fo.veilarbregistrering.metrics.Events.SYKMELDT_BESVARELSE_EVENT;
import static no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType.SYKMELDT;

public class SykmeldtRegistreringService {

    private static final Logger LOG = LoggerFactory.getLogger(SykmeldtRegistreringService.class);

    private final BrukerTilstandService brukerTilstandService;
    private final OppfolgingGateway oppfolgingGateway;
    private final SykmeldtRegistreringRepository sykmeldtRegistreringRepository;
    private final ManuellRegistreringRepository manuellRegistreringRepository;
    private final InfluxMetricsService influxMetricsService;

    public SykmeldtRegistreringService(
            BrukerTilstandService brukerTilstandService,
            OppfolgingGateway oppfolgingGateway,
            SykmeldtRegistreringRepository sykmeldtRegistreringRepository,
            ManuellRegistreringRepository manuellRegistreringRepository,
            InfluxMetricsService influxMetricsService) {
        this.brukerTilstandService = brukerTilstandService;
        this.oppfolgingGateway = oppfolgingGateway;
        this.sykmeldtRegistreringRepository = sykmeldtRegistreringRepository;
        this.manuellRegistreringRepository = manuellRegistreringRepository;
        this.influxMetricsService = influxMetricsService;
    }

    @Transactional
    public long registrerSykmeldt(SykmeldtRegistrering sykmeldtRegistrering, Bruker bruker, NavVeileder navVeileder) {
        validerSykmeldtdRegistrering(sykmeldtRegistrering, bruker);

        oppfolgingGateway.settOppfolgingSykmeldt(bruker.getGjeldendeFoedselsnummer(), sykmeldtRegistrering.getBesvarelse());
        long id = sykmeldtRegistreringRepository.lagreSykmeldtBruker(sykmeldtRegistrering, bruker.getAktorId());

        lagreManuellRegistrering(id, navVeileder);
        registrerOverfortStatistikk(navVeileder);

        LOG.info("Sykmeldtregistrering gjennomført med data {}", sykmeldtRegistrering);
        influxMetricsService.reportFields(SYKMELDT_BESVARELSE_EVENT,
                sykmeldtRegistrering.getBesvarelse().getUtdanning(),
                sykmeldtRegistrering.getBesvarelse().getFremtidigSituasjon());

        return id;
    }

    private void validerSykmeldtdRegistrering(SykmeldtRegistrering sykmeldtRegistrering, Bruker bruker) {
        ofNullable(sykmeldtRegistrering.getBesvarelse())
                .orElseThrow(() -> new RuntimeException("Besvarelse for sykmeldt ugyldig."));

        BrukersTilstand brukersTilstand = brukerTilstandService.hentBrukersTilstand(bruker);

        if (brukersTilstand.ikkeErSykemeldtRegistrering()) {
            throw new RuntimeException("Bruker kan ikke registreres.");
        }
    }

    private void lagreManuellRegistrering(long id, NavVeileder veileder) {
        if (veileder == null) return;

        ManuellRegistrering manuellRegistrering = new ManuellRegistrering()
                .setRegistreringId(id)
                .setBrukerRegistreringType(SYKMELDT)
                .setVeilederIdent(veileder.getVeilederIdent())
                .setVeilederEnhetId(veileder.getEnhetsId());

        manuellRegistreringRepository.lagreManuellRegistrering(manuellRegistrering);
    }

    private void registrerOverfortStatistikk(NavVeileder veileder) {
        if (veileder == null) return;
        influxMetricsService.reportFields(MANUELL_REGISTRERING_EVENT, SYKMELDT);
    }
}
