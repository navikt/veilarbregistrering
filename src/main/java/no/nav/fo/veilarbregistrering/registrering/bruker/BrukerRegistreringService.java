package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.amplitude.AmplitudeLogger;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.StartRegistreringUtils;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService;
import no.nav.fo.veilarbregistrering.registrering.resources.RegistreringTilstandDto;
import no.nav.fo.veilarbregistrering.registrering.resources.StartRegistreringStatusDto;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static java.time.LocalDate.now;
import static java.util.Optional.ofNullable;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.Event.PROFILERING_EVENT;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.Event.START_REGISTRERING_EVENT;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.reportFields;
import static no.nav.fo.veilarbregistrering.metrics.Metrics.reportTags;
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.*;
import static no.nav.fo.veilarbregistrering.registrering.bruker.ValideringUtils.validerBrukerRegistrering;
import static no.nav.fo.veilarbregistrering.registrering.resources.StartRegistreringStatusDtoMapper.map;


public class BrukerRegistreringService {

    private static final Logger LOG = LoggerFactory.getLogger(BrukerRegistreringService.class);

    private final BrukerRegistreringRepository brukerRegistreringRepository;
    private final ProfileringRepository profileringRepository;
    private final UnleashService unleashService;
    private final SykemeldingService sykemeldingService;
    private final PersonGateway personGateway;
    private final ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer;
    private final OppfolgingGateway oppfolgingGateway;
    private final ArbeidsforholdGateway arbeidsforholdGateway;
    private final ManuellRegistreringService manuellRegistreringService;
    private final StartRegistreringUtils startRegistreringUtils;
    private final ArbeidssokerProfilertProducer arbeidssokerProfilertProducer;

    public BrukerRegistreringService(BrukerRegistreringRepository brukerRegistreringRepository,
                                     ProfileringRepository profileringRepository,
                                     OppfolgingGateway oppfolgingGateway,
                                     PersonGateway personGateway,
                                     SykemeldingService sykemeldingService,
                                     ArbeidsforholdGateway arbeidsforholdGateway,
                                     ManuellRegistreringService manuellRegistreringService,
                                     StartRegistreringUtils startRegistreringUtils,
                                     UnleashService unleashService,
                                     ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer,

                                     ArbeidssokerProfilertProducer arbeidssokerProfilertProducer) {
        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.profileringRepository = profileringRepository;
        this.personGateway = personGateway;
        this.unleashService = unleashService;
        this.oppfolgingGateway = oppfolgingGateway;
        this.sykemeldingService = sykemeldingService;
        this.arbeidsforholdGateway = arbeidsforholdGateway;
        this.manuellRegistreringService = manuellRegistreringService;
        this.startRegistreringUtils = startRegistreringUtils;
        this.arbeidssokerRegistrertProducer = arbeidssokerRegistrertProducer;
        this.arbeidssokerProfilertProducer = arbeidssokerProfilertProducer;
    }

    @Transactional
    public void reaktiverBruker(Bruker bruker) {
        BrukersTilstand brukersTilstand = hentBrukersTilstand(bruker.getFoedselsnummer());
        if (!brukersTilstand.kanReaktiveres()) {
            throw new RuntimeException("Bruker kan ikke reaktiveres.");
        }

        brukerRegistreringRepository.lagreReaktiveringForBruker(bruker.getAktorId());
        oppfolgingGateway.reaktiverBruker(bruker.getFoedselsnummer());

        LOG.info("Reaktivering av bruker med aktørId : {}", bruker.getAktorId());
    }

    @Transactional
    public OrdinaerBrukerRegistrering registrerBruker(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering, Bruker bruker) {
        BrukersTilstand brukersTilstand = hentBrukersTilstand(bruker.getFoedselsnummer());

        if (brukersTilstand.isUnderOppfolging()) {
            throw new RuntimeException("Bruker allerede under oppfølging.");
        }

        if (brukersTilstand.ikkeErOrdinaerRegistrering()) {
            throw new RuntimeException(String.format("Brukeren kan ikke registreres ordinært fordi utledet registreringstype er %s.", brukersTilstand.getRegistreringstype()));
        }

        try {
            validerBrukerRegistrering(ordinaerBrukerRegistrering);
        } catch (RuntimeException e) {
            LOG.warn("Ugyldig innsendt registrering. Besvarelse: {} Stilling: {}", ordinaerBrukerRegistrering.getBesvarelse(), ordinaerBrukerRegistrering.getSisteStilling());
            OrdinaerBrukerRegistreringMetrikker.rapporterInvalidRegistrering(ordinaerBrukerRegistrering);
            throw e;
        }

        OrdinaerBrukerRegistrering oppettetBrukerRegistrering;
        if (lagreUtenArenaOverforing()) {
            LOG.info("Oppretter bruker uten synkron overføring til Arena");
            oppettetBrukerRegistrering = mottattBruker(bruker, ordinaerBrukerRegistrering);
        } else {
            LOG.info("Oppretter bruker med synkron overføring til Arena");
            oppettetBrukerRegistrering = opprettBruker(bruker, ordinaerBrukerRegistrering);
        }

        return oppettetBrukerRegistrering;
    }

    BrukersTilstand hentBrukersTilstand(Foedselsnummer fnr) {
        Oppfolgingsstatus oppfolgingsstatus = oppfolgingGateway.hentOppfolgingsstatus(fnr);

        SykmeldtInfoData sykeforloepMetaData = null;
        boolean erSykmeldtMedArbeidsgiver = oppfolgingsstatus.getErSykmeldtMedArbeidsgiver().orElse(false);
        if (erSykmeldtMedArbeidsgiver) {
            sykeforloepMetaData = sykemeldingService.hentSykmeldtInfoData(fnr);
        }

        RegistreringType registreringType = beregnRegistreringType(oppfolgingsstatus, sykeforloepMetaData);

        return new BrukersTilstand(oppfolgingsstatus, sykeforloepMetaData, registreringType);
    }

    public StartRegistreringStatusDto hentStartRegistreringStatus(Foedselsnummer fnr) {
        BrukersTilstand brukersTilstand = hentBrukersTilstand(fnr);

        Optional<GeografiskTilknytning> muligGeografiskTilknytning = hentGeografiskTilknytning(fnr);

        muligGeografiskTilknytning.ifPresent(geografiskTilknytning -> {
            reportFields(START_REGISTRERING_EVENT, brukersTilstand, geografiskTilknytning);
        });

        RegistreringType registreringType = brukersTilstand.getRegistreringstype();

        Boolean oppfyllerBetingelseOmArbeidserfaring = null;
        if (ORDINAER_REGISTRERING.equals(registreringType)) {
            oppfyllerBetingelseOmArbeidserfaring =
                    arbeidsforholdGateway.hentArbeidsforhold(fnr)
                            .harJobbetSammenhengendeSeksAvTolvSisteManeder(now());
        }

        StartRegistreringStatusDto startRegistreringStatus = map(
                brukersTilstand,
                muligGeografiskTilknytning,
                oppfyllerBetingelseOmArbeidserfaring,
                fnr.alder(now()));

        LOG.info("Returnerer startregistreringsstatus {}", startRegistreringStatus);
        return startRegistreringStatus;
    }

    private Optional<GeografiskTilknytning> hentGeografiskTilknytning(Foedselsnummer fnr) {
        Optional<GeografiskTilknytning> geografiskTilknytning = Optional.empty();
        try {
            long t1 = System.currentTimeMillis();
            geografiskTilknytning = personGateway.hentGeografiskTilknytning(fnr);
            LOG.info("Henting av geografisk tilknytning tok {} ms.", System.currentTimeMillis() - t1);

        } catch (RuntimeException e) {
            LOG.warn("Hent geografisk tilknytning feilet. Skal ikke påvirke annen bruk.", e);
        }

        return geografiskTilknytning;
    }

    private OrdinaerBrukerRegistrering opprettBruker(Bruker bruker, OrdinaerBrukerRegistrering brukerRegistrering) {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository.lagre(brukerRegistrering, bruker);

        Profilering profilering = profilerBrukerTilInnsatsgruppe(bruker.getFoedselsnummer(), ordinaerBrukerRegistrering.getBesvarelse());
        profileringRepository.lagreProfilering(ordinaerBrukerRegistrering.getId(), profilering);

        oppfolgingGateway.aktiverBruker(bruker.getFoedselsnummer(), profilering.getInnsatsgruppe());
        reportTags(PROFILERING_EVENT, profilering.getInnsatsgruppe());

        OrdinaerBrukerBesvarelseMetrikker.rapporterOrdinaerBesvarelse(brukerRegistrering, profilering);
        LOG.info("Brukerregistrering gjennomført med data {}, Profilering {}", ordinaerBrukerRegistrering, profilering);

        RegistreringTilstand registreringTilstand = RegistreringTilstand.ofArenaOk(ordinaerBrukerRegistrering.getId());
        brukerRegistreringRepository.lagre(registreringTilstand);

        arbeidssokerRegistrertProducer.publiserArbeidssokerRegistrert(
                bruker.getAktorId(),
                ordinaerBrukerRegistrering.getBrukersSituasjon(),
                ordinaerBrukerRegistrering.getOpprettetDato());

        arbeidssokerProfilertProducer.publiserProfilering(
                bruker.getAktorId(),
                profilering.getInnsatsgruppe(),
                ordinaerBrukerRegistrering.getOpprettetDato());

        return ordinaerBrukerRegistrering;
    }

    private OrdinaerBrukerRegistrering mottattBruker(Bruker bruker, OrdinaerBrukerRegistrering brukerRegistrering) {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository.lagre(brukerRegistrering, bruker);

        Profilering profilering = profilerBrukerTilInnsatsgruppe(bruker.getFoedselsnummer(), ordinaerBrukerRegistrering.getBesvarelse());
        profileringRepository.lagreProfilering(ordinaerBrukerRegistrering.getId(), profilering);
        reportTags(PROFILERING_EVENT, profilering.getInnsatsgruppe());

        OrdinaerBrukerBesvarelseMetrikker.rapporterOrdinaerBesvarelse(brukerRegistrering, profilering);
        LOG.info("Brukerregistrering gjennomført med data {}, Profilering {}", ordinaerBrukerRegistrering, profilering);

        RegistreringTilstand registreringTilstand = RegistreringTilstand.ofMottattRegistrering(ordinaerBrukerRegistrering.getId());
        LOG.info("Lagrer: {}", registreringTilstand);
        brukerRegistreringRepository.lagre(registreringTilstand);

        return ordinaerBrukerRegistrering;
    }

    public void oppdaterRegistreringTilstand(RegistreringTilstandDto registreringTilstandDto) {
        RegistreringTilstand original = brukerRegistreringRepository.hentRegistreringTilstand(registreringTilstandDto.getId());
        RegistreringTilstand oppdatert = original.oppdaterStatus(registreringTilstandDto.getStatus());
        brukerRegistreringRepository.oppdater(oppdatert);
    }

    private Profilering profilerBrukerTilInnsatsgruppe(Foedselsnummer fnr, Besvarelse besvarelse) {
        return startRegistreringUtils.profilerBruker(
                fnr.alder(now()),
                () -> arbeidsforholdGateway.hentArbeidsforhold(fnr),
                now(), besvarelse);
    }

    public BrukerRegistreringWrapper hentBrukerRegistrering(Bruker bruker) {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository
                .hentOrdinaerBrukerregistreringForAktorId(bruker.getAktorId());

        if (ordinaerBrukerRegistrering != null) {
            Profilering profilering = profileringRepository.hentProfileringForId(ordinaerBrukerRegistrering.getId());
            ordinaerBrukerRegistrering.setProfilering(profilering);
        }

        SykmeldtRegistrering sykmeldtBrukerRegistrering = brukerRegistreringRepository
                .hentSykmeldtregistreringForAktorId(bruker.getAktorId());

        setManueltRegistrertAv(ordinaerBrukerRegistrering, sykmeldtBrukerRegistrering);

        if (ordinaerBrukerRegistrering == null && sykmeldtBrukerRegistrering == null) {
            return null;
        } else if (ordinaerBrukerRegistrering == null) {
            return new BrukerRegistreringWrapper(sykmeldtBrukerRegistrering);
        } else if (sykmeldtBrukerRegistrering == null) {
            return new BrukerRegistreringWrapper(ordinaerBrukerRegistrering);
        }

        LocalDateTime profilertBrukerRegistreringDato = ordinaerBrukerRegistrering.getOpprettetDato();
        LocalDateTime sykmeldtRegistreringDato = sykmeldtBrukerRegistrering.getOpprettetDato();

        if (profilertBrukerRegistreringDato.isAfter(sykmeldtRegistreringDato)) {
            return new BrukerRegistreringWrapper(ordinaerBrukerRegistrering);
        } else {
            return new BrukerRegistreringWrapper(sykmeldtBrukerRegistrering);
        }
    }

    private void setManueltRegistrertAv(BrukerRegistrering... registreringer) {
        Arrays.stream(registreringer)
                .filter(Objects::nonNull)
                .forEach((registrering) -> {
                    registrering.setManueltRegistrertAv(manuellRegistreringService
                            .hentManuellRegistreringVeileder(registrering.getId(), registrering.hentType()));
                });
    }

    @Transactional
    public long registrerSykmeldt(SykmeldtRegistrering sykmeldtRegistrering, Bruker bruker) {
        ofNullable(sykmeldtRegistrering.getBesvarelse())
                .orElseThrow(() -> new RuntimeException("Besvarelse for sykmeldt ugyldig."));

        BrukersTilstand brukersTilstand = hentBrukersTilstand(bruker.getFoedselsnummer());

        if (brukersTilstand.ikkeErSykemeldtRegistrering()) {
            throw new RuntimeException("Bruker kan ikke registreres.");
        }

        oppfolgingGateway.settOppfolgingSykmeldt(bruker.getFoedselsnummer(), sykmeldtRegistrering.getBesvarelse());
        long id = brukerRegistreringRepository.lagreSykmeldtBruker(sykmeldtRegistrering, bruker.getAktorId());
        LOG.info("Sykmeldtregistrering gjennomført med data {}", sykmeldtRegistrering);

        if (unleashService.isEnabled("veilarbregistrering.amplitude.test")) {
            AmplitudeLogger.log(bruker.getAktorId());
        }

        return id;
    }

    private boolean lagreUtenArenaOverforing() {
        return unleashService.isEnabled("veilarbregistrering.lagreUtenArenaOverforing");
    }
}
