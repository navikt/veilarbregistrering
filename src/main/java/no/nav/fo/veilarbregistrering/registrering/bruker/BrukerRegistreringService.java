package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.apiapp.security.veilarbabac.Bruker;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning;
import no.nav.fo.veilarbregistrering.bruker.PersonGateway;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.StartRegistreringUtils;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService;
import no.nav.fo.veilarbregistrering.registrering.resources.StartRegistreringStatusDto;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;
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
import static no.nav.fo.veilarbregistrering.metrics.Metrics.report;
import static no.nav.fo.veilarbregistrering.registrering.bruker.FnrUtils.utledAlderForFnr;
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.ORDINAER_REGISTRERING;
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.beregnRegistreringType;
import static no.nav.fo.veilarbregistrering.registrering.resources.StartRegistreringStatusDtoMapper.map;
import static no.nav.fo.veilarbregistrering.registrering.bruker.ValideringUtils.validerBrukerRegistrering;


public class BrukerRegistreringService {

    private static final Logger LOG = LoggerFactory.getLogger(BrukerRegistreringService.class);

    private final BrukerRegistreringRepository brukerRegistreringRepository;
    private final ProfileringRepository profileringRepository;
    private final RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature;
    private final SykemeldingService sykemeldingService;
    private final PersonGateway personGateway;
    private OppfolgingGateway oppfolgingGateway;
    private ArbeidsforholdGateway arbeidsforholdGateway;
    private ManuellRegistreringService manuellRegistreringService;
    private StartRegistreringUtils startRegistreringUtils;

    public BrukerRegistreringService(BrukerRegistreringRepository brukerRegistreringRepository,
                                     ProfileringRepository profileringRepository,
                                     OppfolgingGateway oppfolgingGateway,
                                     PersonGateway personGateway, SykemeldingService sykemeldingService,
                                     ArbeidsforholdGateway arbeidsforholdGateway,
                                     ManuellRegistreringService manuellRegistreringService,
                                     StartRegistreringUtils startRegistreringUtils,
                                     RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature

    ) {
        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.profileringRepository = profileringRepository;
        this.personGateway = personGateway;
        this.sykemeldtRegistreringFeature = sykemeldtRegistreringFeature;
        this.oppfolgingGateway = oppfolgingGateway;
        this.sykemeldingService = sykemeldingService;
        this.arbeidsforholdGateway = arbeidsforholdGateway;
        this.manuellRegistreringService = manuellRegistreringService;
        this.startRegistreringUtils = startRegistreringUtils;
    }

    @Transactional
    public void reaktiverBruker(Bruker bruker) {

        BrukersTilstand brukersTilstand = hentBrukersTilstand(bruker.getFoedselsnummer());
        if (!brukersTilstand.kanReaktiveres()) {
            throw new RuntimeException("Bruker kan ikke reaktiveres.");
        }

        AktorId aktorId = new AktorId(bruker.getAktoerId());

        brukerRegistreringRepository.lagreReaktiveringForBruker(aktorId);
        oppfolgingGateway.reaktiverBruker(bruker.getFoedselsnummer());

        LOG.info("Reaktivering av bruker med aktørId : {}", aktorId);
    }

    @Transactional
    public OrdinaerBrukerRegistrering registrerBruker(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering, Bruker bruker) {

        BrukersTilstand brukersTilstand = hentBrukersTilstand(bruker.getFoedselsnummer());

        if (brukersTilstand.isUnderOppfolging()) {
            throw new RuntimeException("Bruker allerede under oppfølging.");
        }

        if (!brukersTilstand.erOrdinaerRegistrering()) {
            throw new RuntimeException("Brukeren kan ikke registreres. Krever registreringtypen ordinær.");
        }

        try {
            validerBrukerRegistrering(ordinaerBrukerRegistrering);
        } catch (RuntimeException e) {
            LOG.warn("Ugyldig innsendt registrering. Besvarelse: {} Stilling: {}", ordinaerBrukerRegistrering.getBesvarelse(), ordinaerBrukerRegistrering.getSisteStilling());
            OrdinaerBrukerRegistreringMetrikker.rapporterInvalidRegistrering(ordinaerBrukerRegistrering);
            throw e;
        }

        return opprettBruker(bruker, ordinaerBrukerRegistrering);
    }

    BrukersTilstand hentBrukersTilstand(String fnr) {
        Oppfolgingsstatus oppfolgingsstatus = oppfolgingGateway.hentOppfolgingsstatus(fnr);

        SykmeldtInfoData sykeforloepMetaData = null;
        boolean erSykmeldtMedArbeidsgiver = oppfolgingsstatus.getErSykmeldtMedArbeidsgiver().orElse(false);
        if (erSykmeldtMedArbeidsgiver) {
            if (sykemeldtRegistreringFeature.erSykemeldtRegistreringAktiv()) {
                sykeforloepMetaData = sykemeldingService.hentSykmeldtInfoData(fnr);
            }
        }

        RegistreringType registreringType = beregnRegistreringType(oppfolgingsstatus, sykeforloepMetaData);

        return new BrukersTilstand(oppfolgingsstatus, sykeforloepMetaData, registreringType);
    }

    public StartRegistreringStatusDto hentStartRegistreringStatus(String fnr) {
        BrukersTilstand brukersTilstand = hentBrukersTilstand(fnr);

        Optional<GeografiskTilknytning> muligGeografiskTilknytning = hentGeografiskTilknytning(fnr);

        muligGeografiskTilknytning.ifPresent(geografiskTilknytning -> {
            report(START_REGISTRERING_EVENT, brukersTilstand, geografiskTilknytning);
        });

        RegistreringType registreringType = brukersTilstand.getRegistreringstype();

        Boolean oppfyllerBetingelseOmArbeidserfaring = null;
        if (ORDINAER_REGISTRERING.equals(registreringType)) {
            oppfyllerBetingelseOmArbeidserfaring = startRegistreringUtils.harJobbetSammenhengendeSeksAvTolvSisteManeder(
                    () -> arbeidsforholdGateway.hentArbeidsforhold(fnr),
                    now());
        }

        StartRegistreringStatusDto startRegistreringStatus = map(
                brukersTilstand, muligGeografiskTilknytning, oppfyllerBetingelseOmArbeidserfaring);

        LOG.info("Returnerer startregistreringsstatus {}", startRegistreringStatus);
        return startRegistreringStatus;
    }

    private Optional<GeografiskTilknytning> hentGeografiskTilknytning(String fnr) {
        Optional<GeografiskTilknytning> geografiskTilknytning = Optional.empty();
        try {
            long t1 = System.currentTimeMillis();
            geografiskTilknytning = personGateway.hentGeografiskTilknytning(Foedselsnummer.of(fnr));
            LOG.info("Henting av geografisk tilknytning tok {} ms.", System.currentTimeMillis() - t1);

        } catch (RuntimeException e) {
            LOG.warn("Hent geografisk tilknytning feilet. Skal ikke påvirke annen bruk.", e);
        }

        return geografiskTilknytning;
    }

    private OrdinaerBrukerRegistrering opprettBruker(Bruker bruker, OrdinaerBrukerRegistrering brukerRegistrering) {
        AktorId aktorId = new AktorId(bruker.getAktoerId());

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository.lagreOrdinaerBruker(brukerRegistrering, aktorId);

        Profilering profilering = profilerBrukerTilInnsatsgruppe(bruker.getFoedselsnummer(), ordinaerBrukerRegistrering.getBesvarelse());
        profileringRepository.lagreProfilering(ordinaerBrukerRegistrering.getId(), profilering);

        oppfolgingGateway.aktiverBruker(bruker.getFoedselsnummer(), profilering.getInnsatsgruppe());
        report(PROFILERING_EVENT, profilering.getInnsatsgruppe());

        OrdinaerBrukerBesvarelseMetrikker.rapporterOrdinaerBesvarelse(brukerRegistrering, profilering);
        LOG.info("Brukerregistrering gjennomført med data {}, Profilering {}", ordinaerBrukerRegistrering, profilering);
        return ordinaerBrukerRegistrering;
    }

    private void setManueltRegistrertAv(BrukerRegistrering...registreringer){
        Arrays.stream(registreringer)
                .filter(Objects::nonNull)
                .forEach((registrering) -> {
                    registrering.setManueltRegistrertAv(manuellRegistreringService
                            .hentManuellRegistreringVeileder(registrering.getId(), registrering.hentType()));
                });
    }

    public BrukerRegistreringWrapper hentBrukerRegistrering(Bruker bruker) {

        AktorId aktorId = new AktorId(bruker.getAktoerId());

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository
                .hentOrdinaerBrukerregistreringForAktorId(aktorId);

        if (ordinaerBrukerRegistrering != null) {
            Profilering profilering = profileringRepository.hentProfileringForId(ordinaerBrukerRegistrering.getId());
            ordinaerBrukerRegistrering.setProfilering(profilering);
        }

        SykmeldtRegistrering sykmeldtBrukerRegistrering = brukerRegistreringRepository
                .hentSykmeldtregistreringForAktorId(aktorId);

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

    private Profilering profilerBrukerTilInnsatsgruppe(String fnr, Besvarelse besvarelse) {
        return startRegistreringUtils.profilerBruker(
                utledAlderForFnr(fnr, now()),
                () -> arbeidsforholdGateway.hentArbeidsforhold(fnr),
                now(), besvarelse);
    }

    @Transactional
    public long registrerSykmeldt(SykmeldtRegistrering sykmeldtRegistrering, Bruker bruker) {
        if (!sykemeldtRegistreringFeature.erSykemeldtRegistreringAktiv()) {
            throw new RuntimeException("Tjenesten for sykmeldt-registrering er togglet av.");
        }

        ofNullable(sykmeldtRegistrering.getBesvarelse())
                .orElseThrow(() -> new RuntimeException("Besvarelse for sykmeldt ugyldig."));

        BrukersTilstand brukersTilstand = hentBrukersTilstand(bruker.getFoedselsnummer());

        if (!brukersTilstand.erRegistrertSomSykmeldtMedArbeidsgiver()) {
            throw new RuntimeException("Bruker kan ikke registreres.");
        }

        oppfolgingGateway.settOppfolgingSykmeldt(bruker.getFoedselsnummer(), sykmeldtRegistrering.getBesvarelse());
        AktorId aktorId = new AktorId(bruker.getAktoerId());
        long id = brukerRegistreringRepository.lagreSykmeldtBruker(sykmeldtRegistrering, aktorId);
        LOG.info("Sykmeldtregistrering gjennomført med data {}", sykmeldtRegistrering);

        return id;
    }

}
