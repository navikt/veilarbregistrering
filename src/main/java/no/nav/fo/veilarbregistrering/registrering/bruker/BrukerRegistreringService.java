package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.ProfileringService;
import no.nav.fo.veilarbregistrering.registrering.resources.RegistreringTilstandDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.time.LocalDate.now;
import static java.util.Optional.ofNullable;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.Event.PROFILERING_EVENT;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.reportTags;


public class BrukerRegistreringService {

    private static final Logger LOG = LoggerFactory.getLogger(BrukerRegistreringService.class);

    private final BrukerRegistreringRepository brukerRegistreringRepository;
    private final AktiveringTilstandRepository aktiveringTilstandRepository;
    private final ProfileringService profileringService;
    private final ProfileringRepository profileringRepository;
    private final OppfolgingGateway oppfolgingGateway;
    private final BrukerTilstandService brukerTilstandService;
    private final ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer;
    private final ArbeidssokerProfilertProducer arbeidssokerProfilertProducer;

    public BrukerRegistreringService(BrukerRegistreringRepository brukerRegistreringRepository,
                                     ProfileringRepository profileringRepository,
                                     OppfolgingGateway oppfolgingGateway,
                                     ProfileringService profileringService,
                                     ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer,
                                     ArbeidssokerProfilertProducer arbeidssokerProfilertProducer,
                                     AktiveringTilstandRepository aktiveringTilstandRepository,
                                     BrukerTilstandService brukerTilstandService) {
        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.profileringRepository = profileringRepository;
        this.oppfolgingGateway = oppfolgingGateway;
        this.profileringService = profileringService;
        this.arbeidssokerRegistrertProducer = arbeidssokerRegistrertProducer;
        this.arbeidssokerProfilertProducer = arbeidssokerProfilertProducer;
        this.aktiveringTilstandRepository = aktiveringTilstandRepository;
        this.brukerTilstandService = brukerTilstandService;
    }

    @Transactional
    public void reaktiverBruker(Bruker bruker) {
        BrukersTilstand brukersTilstand = brukerTilstandService.hentBrukersTilstand(bruker.getGjeldendeFoedselsnummer());
        if (!brukersTilstand.kanReaktiveres()) {
            throw new RuntimeException("Bruker kan ikke reaktiveres.");
        }

        brukerRegistreringRepository.lagreReaktiveringForBruker(bruker.getAktorId());
        oppfolgingGateway.reaktiverBruker(bruker.getGjeldendeFoedselsnummer());

        LOG.info("Reaktivering av bruker med aktørId : {}", bruker.getAktorId());
    }

    @Transactional
    public OrdinaerBrukerRegistrering registrerBruker(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering, Bruker bruker) {
        validerBrukerRegistrering(ordinaerBrukerRegistrering, bruker);

        OrdinaerBrukerRegistrering oppettetBrukerRegistrering = brukerRegistreringRepository.lagre(ordinaerBrukerRegistrering, bruker);

        Profilering profilering = profilerBrukerTilInnsatsgruppe(bruker.getGjeldendeFoedselsnummer(), oppettetBrukerRegistrering.getBesvarelse());
        profileringRepository.lagreProfilering(oppettetBrukerRegistrering.getId(), profilering);

        oppfolgingGateway.aktiverBruker(bruker.getGjeldendeFoedselsnummer(), profilering.getInnsatsgruppe());
        reportTags(PROFILERING_EVENT, profilering.getInnsatsgruppe());

        OrdinaerBrukerBesvarelseMetrikker.rapporterOrdinaerBesvarelse(ordinaerBrukerRegistrering, profilering);
        LOG.info("Brukerregistrering gjennomført med data {}, Profilering {}", oppettetBrukerRegistrering, profilering);

        AktiveringTilstand registreringTilstand = AktiveringTilstand.ofArenaOk(oppettetBrukerRegistrering.getId());
        aktiveringTilstandRepository.lagre(registreringTilstand);

        arbeidssokerRegistrertProducer.publiserArbeidssokerRegistrert(
                new ArbeidssokerRegistrertInternalEvent(
                        bruker.getAktorId(),
                        oppettetBrukerRegistrering.getBesvarelse(),
                        oppettetBrukerRegistrering.getOpprettetDato()));

        arbeidssokerProfilertProducer.publiserProfilering(
                bruker.getAktorId(),
                profilering.getInnsatsgruppe(),
                oppettetBrukerRegistrering.getOpprettetDato());

        return oppettetBrukerRegistrering;
    }

    private void validerBrukerRegistrering(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering, Bruker bruker) {
        BrukersTilstand brukersTilstand = brukerTilstandService.hentBrukersTilstand(bruker.getGjeldendeFoedselsnummer());

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
            OrdinaerBrukerRegistreringMetrikker.rapporterInvalidRegistrering(ordinaerBrukerRegistrering);
            throw e;
        }
    }

    private Profilering profilerBrukerTilInnsatsgruppe(Foedselsnummer fnr, Besvarelse besvarelse) {
        return profileringService.profilerBruker(
                fnr.alder(now()),
                fnr,
                besvarelse);
    }

    @Transactional
    public long registrerSykmeldt(SykmeldtRegistrering sykmeldtRegistrering, Bruker bruker) {
        ofNullable(sykmeldtRegistrering.getBesvarelse())
                .orElseThrow(() -> new RuntimeException("Besvarelse for sykmeldt ugyldig."));

        BrukersTilstand brukersTilstand = brukerTilstandService.hentBrukersTilstand(bruker.getGjeldendeFoedselsnummer());

        if (brukersTilstand.ikkeErSykemeldtRegistrering()) {
            throw new RuntimeException("Bruker kan ikke registreres.");
        }

        oppfolgingGateway.settOppfolgingSykmeldt(bruker.getGjeldendeFoedselsnummer(), sykmeldtRegistrering.getBesvarelse());
        long id = brukerRegistreringRepository.lagreSykmeldtBruker(sykmeldtRegistrering, bruker.getAktorId());
        LOG.info("Sykmeldtregistrering gjennomført med data {}", sykmeldtRegistrering);

        return id;
    }

    public void oppdaterRegistreringTilstand(RegistreringTilstandDto registreringTilstandDto) {
        AktiveringTilstand original = aktiveringTilstandRepository.hentAktiveringTilstand(registreringTilstandDto.getId());
        AktiveringTilstand oppdatert = original.oppdaterStatus(registreringTilstandDto.getStatus());
        aktiveringTilstandRepository.oppdater(oppdatert);
    }

    public List<AktiveringTilstand> finnAktiveringTilstandMed(Status status) {
        return aktiveringTilstandRepository.finnAktiveringTilstandMed(status);
    }
}
