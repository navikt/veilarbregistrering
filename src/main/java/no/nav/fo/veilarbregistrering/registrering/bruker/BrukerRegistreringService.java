package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.metrics.Events;
import no.nav.fo.veilarbregistrering.metrics.InfluxMetricsService;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.ProfileringService;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistrering;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository;
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstand;
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringTilstandRepository;
import no.nav.fo.veilarbregistrering.registrering.formidling.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import static java.time.LocalDate.now;
import static no.nav.fo.veilarbregistrering.registrering.BrukerRegistreringType.ORDINAER;


public class BrukerRegistreringService {

    private static final Logger LOG = LoggerFactory.getLogger(BrukerRegistreringService.class);

    private final BrukerRegistreringRepository brukerRegistreringRepository;
    private final RegistreringTilstandRepository registreringTilstandRepository;
    private final ProfileringService profileringService;
    private final ProfileringRepository profileringRepository;
    private final OppfolgingGateway oppfolgingGateway;
    private final BrukerTilstandService brukerTilstandService;
    private final ManuellRegistreringRepository manuellRegistreringRepository;
    private final InfluxMetricsService influxMetricsService;

    public BrukerRegistreringService(BrukerRegistreringRepository brukerRegistreringRepository,
                                     ProfileringRepository profileringRepository,
                                     OppfolgingGateway oppfolgingGateway,
                                     ProfileringService profileringService,
                                     RegistreringTilstandRepository registreringTilstandRepository,
                                     BrukerTilstandService brukerTilstandService, ManuellRegistreringRepository manuellRegistreringRepository,
                                     InfluxMetricsService influxMetricsService) {
        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.profileringRepository = profileringRepository;
        this.oppfolgingGateway = oppfolgingGateway;
        this.profileringService = profileringService;
        this.registreringTilstandRepository = registreringTilstandRepository;
        this.brukerTilstandService = brukerTilstandService;
        this.manuellRegistreringRepository = manuellRegistreringRepository;

        this.influxMetricsService = influxMetricsService;
    }

    private void registrerOverfortStatistikk(NavVeileder veileder) {
        if (veileder == null) return;
        influxMetricsService.reportFields(Events.MANUELL_REGISTRERING_EVENT, ORDINAER);
    }

    @Transactional
    public OrdinaerBrukerRegistrering registrerBrukerUtenOverforing(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering, Bruker bruker, NavVeileder veileder) {
        validerBrukerRegistrering(ordinaerBrukerRegistrering, bruker);

        OrdinaerBrukerRegistrering opprettetBrukerRegistrering = brukerRegistreringRepository.lagre(ordinaerBrukerRegistrering, bruker);

        lagreManuellRegistrering(opprettetBrukerRegistrering, veileder);

        Profilering profilering = profilerBrukerTilInnsatsgruppe(bruker.getGjeldendeFoedselsnummer(), opprettetBrukerRegistrering.getBesvarelse());
        profileringRepository.lagreProfilering(opprettetBrukerRegistrering.getId(), profilering);

        influxMetricsService.reportTags(Events.PROFILERING_EVENT, profilering.getInnsatsgruppe());

        OrdinaerBrukerBesvarelseMetrikker.rapporterOrdinaerBesvarelse(influxMetricsService,ordinaerBrukerRegistrering, profilering);

        RegistreringTilstand registreringTilstand = RegistreringTilstand.medStatus(Status.MOTTATT, opprettetBrukerRegistrering.getId());
        registreringTilstandRepository.lagre(registreringTilstand);

        LOG.info("Brukerregistrering (id: {}) gjennomført med data {}, Profilering {}", opprettetBrukerRegistrering.getId(), opprettetBrukerRegistrering, profilering);

        return opprettetBrukerRegistrering;
    }

    private void lagreManuellRegistrering(OrdinaerBrukerRegistrering brukerRegistrering, NavVeileder veileder) {
        if (veileder == null) return;

        ManuellRegistrering manuellRegistrering = new ManuellRegistrering()
                .setRegistreringId(brukerRegistrering.id)
                .setBrukerRegistreringType(brukerRegistrering.hentType())
                .setVeilederIdent(veileder.getVeilederIdent())
                .setVeilederEnhetId(veileder.getEnhetsId());

        manuellRegistreringRepository.lagreManuellRegistrering(manuellRegistrering);
    }

    @Transactional(noRollbackFor = {AktiverBrukerException.class})
    public void overforArena(long registreringId, Bruker bruker, NavVeileder veileder) {

        RegistreringTilstand registreringTilstand = overforArena(registreringId, bruker);

        if (registreringTilstand.getStatus() == Status.OVERFORT_ARENA) {
            registrerOverfortStatistikk(veileder);
            return;
        }

        throw new AktiverBrukerException(AktiverBrukerFeil.fromStatus(registreringTilstand.getStatus()));
    }

    private RegistreringTilstand overforArena(long registreringId, Bruker bruker) {

        Profilering profilering = profileringRepository.hentProfileringForId(registreringId);

        try {
            oppfolgingGateway.aktiverBruker(bruker.getGjeldendeFoedselsnummer(), profilering.getInnsatsgruppe());
        } catch (AktiverBrukerException e) {
            RegistreringTilstand oppdatertRegistreringTilstand = oppdaterRegistreringTilstand(registreringId, Status.Companion.from(e.getAktiverBrukerFeil()));

            LOG.info("Overføring av registrering (id: {}) til Arena feilet med {}", registreringId, e.getAktiverBrukerFeil());

            return oppdatertRegistreringTilstand;
        }

        RegistreringTilstand oppdatertRegistreringTilstand = oppdaterRegistreringTilstand(registreringId, Status.OVERFORT_ARENA);

        LOG.info("Overføring av registrering (id: {}) til Arena gjennomført", registreringId);

        return oppdatertRegistreringTilstand;
    }

    private RegistreringTilstand oppdaterRegistreringTilstand(long registreringId, Status status) {
        RegistreringTilstand aktiveringTilstand = registreringTilstandRepository
                .hentTilstandFor(registreringId)
                .oppdaterStatus(status);

        return registreringTilstandRepository.oppdater(aktiveringTilstand);
    }

    private void validerBrukerRegistrering(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering, Bruker bruker) {
        BrukersTilstand brukersTilstand = brukerTilstandService.hentBrukersTilstand(bruker);

        if (brukersTilstand.isUnderOppfolging()) {
            throw new RuntimeException("Bruker allerede under oppfølging.");
        }

        if (brukersTilstand.ikkeErOrdinaerRegistrering()) {
            throw new RuntimeException(String.format("Brukeren kan ikke registreres ordinært fordi utledet registreringstype er %s.", brukersTilstand.getRegistreringstype()));
        }

        try {
            ValideringUtils.validerBrukerRegistrering(ordinaerBrukerRegistrering);
        } catch (RuntimeException e) {
            LOG.warn("Ugyldig innsendt registrering. Besvarelse: {} Stilling: {}", ordinaerBrukerRegistrering.getBesvarelse(), ordinaerBrukerRegistrering.getSisteStilling());
            OrdinaerBrukerRegistreringMetrikker.rapporterInvalidRegistrering(influxMetricsService, ordinaerBrukerRegistrering);
            throw e;
        }
    }

    private Profilering profilerBrukerTilInnsatsgruppe(Foedselsnummer fnr, Besvarelse besvarelse) {
        return profileringService.profilerBruker(
                fnr.alder(now()),
                fnr,
                besvarelse);
    }
}