package no.nav.fo.veilarbregistrering.registrering.bruker;

import lombok.extern.slf4j.Slf4j;
import no.nav.apiapp.security.veilarbabac.Bruker;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.*;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.FremtidigSituasjonSvar;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.InfotrygdData;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient;
import no.nav.fo.veilarbregistrering.utils.AutentiseringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;

import static java.time.LocalDate.now;
import static java.util.Optional.ofNullable;
import static no.nav.fo.veilarbregistrering.oppfolging.adapter.SykmeldtBrukerType.SKAL_TIL_NY_ARBEIDSGIVER;
import static no.nav.fo.veilarbregistrering.oppfolging.adapter.SykmeldtBrukerType.SKAL_TIL_SAMME_ARBEIDSGIVER;
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.ORDINAER_REGISTRERING;
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.SYKMELDT_REGISTRERING;
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.beregnRegistreringType;
import static no.nav.fo.veilarbregistrering.registrering.bruker.ValideringUtils.validerBrukerRegistrering;
import static no.nav.fo.veilarbregistrering.registrering.bruker.FnrUtils.utledAlderForFnr;
import static no.nav.fo.veilarbregistrering.utils.FunksjonelleMetrikker.*;


@Slf4j
public class BrukerRegistreringService {

    private final BrukerRegistreringRepository brukerRegistreringRepository;
    private final ProfileringRepository profileringRepository;
    private final RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature;
    private OppfolgingClient oppfolgingClient;
    private SykmeldtInfoClient sykmeldtInfoClient;
    private ArbeidsforholdGateway arbeidsforholdGateway;
    private ManuellRegistreringService manuellRegistreringService;
    private StartRegistreringUtils startRegistreringUtils;

    public BrukerRegistreringService(BrukerRegistreringRepository brukerRegistreringRepository,
                                     ProfileringRepository profileringRepository,
                                     OppfolgingClient oppfolgingClient,
                                     SykmeldtInfoClient sykmeldtInfoClient,
                                     ArbeidsforholdGateway arbeidsforholdGateway,
                                     ManuellRegistreringService manuellRegistreringService,
                                     StartRegistreringUtils startRegistreringUtils,
                                     RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature

    ) {
        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.profileringRepository = profileringRepository;
        this.sykemeldtRegistreringFeature = sykemeldtRegistreringFeature;
        this.oppfolgingClient = oppfolgingClient;
        this.sykmeldtInfoClient = sykmeldtInfoClient;
        this.arbeidsforholdGateway = arbeidsforholdGateway;
        this.manuellRegistreringService = manuellRegistreringService;
        this.startRegistreringUtils = startRegistreringUtils;
    }

    @Transactional
    public void reaktiverBruker(Bruker bruker) {

        Boolean kanReaktiveres = hentStartRegistreringStatus(bruker.getFoedselsnummer()).getRegistreringType() == RegistreringType.REAKTIVERING;
        if (!kanReaktiveres) {
            throw new RuntimeException("Bruker kan ikke reaktiveres.");
        }

        AktorId aktorId = new AktorId(bruker.getAktoerId());

        brukerRegistreringRepository.lagreReaktiveringForBruker(aktorId);
        oppfolgingClient.reaktiverBruker(bruker.getFoedselsnummer());

        log.info("Reaktivering av bruker med aktørId : {}", aktorId);
    }

    @Transactional
    public OrdinaerBrukerRegistrering registrerBruker(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering, Bruker bruker) {

        StartRegistreringStatus startRegistreringStatus = hentStartRegistreringStatus(bruker.getFoedselsnummer());

        if (startRegistreringStatus.isUnderOppfolging()) {
            throw new RuntimeException("Bruker allerede under oppfølging.");
        }

        if (!ORDINAER_REGISTRERING.equals(startRegistreringStatus.getRegistreringType())) {
            throw new RuntimeException("Brukeren kan ikke registreres. Krever registreringtypen ordinær.");
        }

        try {
            validerBrukerRegistrering(ordinaerBrukerRegistrering);
        } catch (RuntimeException e) {
            log.warn("Ugyldig innsendt registrering. Besvarelse: {} Stilling: {}", ordinaerBrukerRegistrering.getBesvarelse(), ordinaerBrukerRegistrering.getSisteStilling());
            rapporterInvalidRegistrering(ordinaerBrukerRegistrering);
            throw e;
        }

        Profilering profilering = profilerBrukerTilInnsatsgruppe(bruker.getFoedselsnummer(), ordinaerBrukerRegistrering);

        return opprettBruker(bruker, ordinaerBrukerRegistrering, profilering);
    }

    public StartRegistreringStatus hentStartRegistreringStatus(String fnr) {
        OppfolgingStatusData oppfolgingStatusData = oppfolgingClient.hentOppfolgingsstatus(fnr);

        SykmeldtInfoData sykeforloepMetaData = null;
        String maksDato = "";
        boolean erSykmeldtMedArbeidsgiver = ofNullable(oppfolgingStatusData.erSykmeldtMedArbeidsgiver).orElse(false);
        if (erSykmeldtMedArbeidsgiver) {
            if (sykemeldtRegistreringFeature.erSykemeldtRegistreringAktiv()) {
                sykeforloepMetaData = hentSykmeldtInfoData(fnr);
                maksDato = sykeforloepMetaData.maksDato;
            }
        }

        RegistreringType registreringType = beregnRegistreringType(oppfolgingStatusData, sykeforloepMetaData);

        StartRegistreringStatus startRegistreringStatus = new StartRegistreringStatus()
                .setUnderOppfolging(oppfolgingStatusData.isUnderOppfolging())
                .setRegistreringType(registreringType)
                .setErSykmeldtMedArbeidsgiver(erSykmeldtMedArbeidsgiver)
                .setMaksDato(maksDato);

        if (ORDINAER_REGISTRERING.equals(registreringType)) {
            boolean oppfyllerBetingelseOmArbeidserfaring = startRegistreringUtils.harJobbetSammenhengendeSeksAvTolvSisteManeder(
                    () -> arbeidsforholdGateway.hentArbeidsforhold(fnr),
                    now());
            startRegistreringStatus.setJobbetSeksAvTolvSisteManeder(oppfyllerBetingelseOmArbeidserfaring);
        }

        log.info("Returnerer startregistreringsstatus {}", startRegistreringStatus);
        return startRegistreringStatus;
    }

    private OrdinaerBrukerRegistrering opprettBruker(Bruker bruker, OrdinaerBrukerRegistrering brukerRegistrering, Profilering profilering) {
        AktorId aktorId = new AktorId(bruker.getAktoerId());

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository.lagreOrdinaerBruker(brukerRegistrering, aktorId);
        profileringRepository.lagreProfilering(ordinaerBrukerRegistrering.getId(), profilering);
        oppfolgingClient.aktiverBruker(new AktiverBrukerData(new Fnr(bruker.getFoedselsnummer()), profilering.getInnsatsgruppe()));

        rapporterProfilering(profilering);
        rapporterOrdinaerBesvarelse(brukerRegistrering, profilering);
        log.info("Brukerregistrering gjennomført med data {}, Profilering {}", ordinaerBrukerRegistrering, profilering);
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

    private Profilering profilerBrukerTilInnsatsgruppe(String fnr, OrdinaerBrukerRegistrering bruker) {
        return startRegistreringUtils.profilerBruker(
                bruker,
                utledAlderForFnr(fnr, now()),
                () -> arbeidsforholdGateway.hentArbeidsforhold(fnr),
                now());
    }

    @Transactional
    public long registrerSykmeldt(SykmeldtRegistrering sykmeldtRegistrering, Bruker bruker) {
        if (!sykemeldtRegistreringFeature.erSykemeldtRegistreringAktiv()) {
            throw new RuntimeException("Tjenesten for sykmeldt-registrering er togglet av.");
        }

        ofNullable(sykmeldtRegistrering.getBesvarelse())
                .orElseThrow(() -> new RuntimeException("Besvarelse for sykmeldt ugyldig."));

        StartRegistreringStatus startRegistreringStatus = hentStartRegistreringStatus(bruker.getFoedselsnummer());
        long id;

        if (SYKMELDT_REGISTRERING.equals(startRegistreringStatus.getRegistreringType())) {
            AktorId aktorId = new AktorId(bruker.getAktoerId());
            SykmeldtBrukerType sykmeldtBrukerType = finnSykmeldtBrukerType(sykmeldtRegistrering);
            oppfolgingClient.settOppfolgingSykmeldt(sykmeldtBrukerType, bruker.getFoedselsnummer());
            id = brukerRegistreringRepository.lagreSykmeldtBruker(sykmeldtRegistrering, aktorId);
            log.info("Sykmeldtregistrering gjennomført med data {}", sykmeldtRegistrering);
        } else {
            throw new RuntimeException("Bruker kan ikke registreres.");
        }

        return id;
    }

    //FIXME: Flytt denne ut i en Gateway som mapper mellom intern og ekstern modell.
    SykmeldtBrukerType finnSykmeldtBrukerType(SykmeldtRegistrering sykmeldtRegistrering) {
        FremtidigSituasjonSvar fremtidigSituasjon = sykmeldtRegistrering.getBesvarelse().getFremtidigSituasjon();
        if  (fremtidigSituasjon == FremtidigSituasjonSvar.SAMME_ARBEIDSGIVER
                || fremtidigSituasjon == FremtidigSituasjonSvar.SAMME_ARBEIDSGIVER_NY_STILLING
                || fremtidigSituasjon == FremtidigSituasjonSvar.INGEN_PASSER
        ) {
            return SKAL_TIL_SAMME_ARBEIDSGIVER;
        } else {
            return SKAL_TIL_NY_ARBEIDSGIVER;
        }

    }

    public SykmeldtInfoData hentSykmeldtInfoData(String fnr) {

        SykmeldtInfoData sykmeldtInfoData = new SykmeldtInfoData();

        if (AutentiseringUtils.erVeileder()) {
            // Veiledere har ikke tilgang til å gjøre kall mot infotrygd
            // Sett inngang aktiv, slik at de får registrert sykmeldte brukere
            sykmeldtInfoData.setErArbeidsrettetOppfolgingSykmeldtInngangAktiv(true);
        } else {
            InfotrygdData infotrygdData = sykmeldtInfoClient.hentSykmeldtInfoData(fnr);
            boolean erSykmeldtOver39Uker = beregnSykmeldtMellom39Og52Uker(infotrygdData.maksDato, now());

            sykmeldtInfoData.setMaksDato(infotrygdData.maksDato);
            sykmeldtInfoData.setErArbeidsrettetOppfolgingSykmeldtInngangAktiv(erSykmeldtOver39Uker);
        }

        return sykmeldtInfoData;
    }

    static boolean beregnSykmeldtMellom39Og52Uker(String maksDato, LocalDate dagenDato) {
        if (maksDato == null) {
            return false;
        }
        LocalDate dato = LocalDate.parse(maksDato);
        long GJENSTAENDE_UKER = 13;

        return ChronoUnit.WEEKS.between(dagenDato, dato) >= 0 &&
                ChronoUnit.WEEKS.between(dagenDato, dato) <= GJENSTAENDE_UKER;
    }
}
