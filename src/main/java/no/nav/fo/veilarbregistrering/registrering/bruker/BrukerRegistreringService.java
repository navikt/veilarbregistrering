package no.nav.fo.veilarbregistrering.registrering.bruker;

import lombok.extern.slf4j.Slf4j;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.InfotrygdData;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.*;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService;
import no.nav.fo.veilarbregistrering.utils.AutentiseringUtils;
import no.nav.fo.veilarbregistrering.utils.DateUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

import static java.time.LocalDate.now;
import static java.util.Optional.ofNullable;
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.ORDINAER_REGISTRERING;
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.SYKMELDT_REGISTRERING;
import static no.nav.fo.veilarbregistrering.registrering.bruker.StartRegistreringUtils.beregnRegistreringType;
import static no.nav.fo.veilarbregistrering.registrering.bruker.ValideringUtils.validerBrukerRegistrering;
import static no.nav.fo.veilarbregistrering.utils.FnrUtils.getAktorIdOrElseThrow;
import static no.nav.fo.veilarbregistrering.utils.FnrUtils.utledAlderForFnr;
import static no.nav.fo.veilarbregistrering.utils.FunksjonelleMetrikker.*;


@Slf4j
public class BrukerRegistreringService {

    private final ArbeidssokerregistreringRepository arbeidssokerregistreringRepository;
    private final AktorService aktorService;
    private final RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature;
    private OppfolgingClient oppfolgingClient;
    private SykmeldtInfoClient sykmeldtInfoClient;
    private ArbeidsforholdGateway arbeidsforholdGateway;
    private ManuellRegistreringService manuellRegistreringService;
    private StartRegistreringUtils startRegistreringUtils;

    public BrukerRegistreringService(ArbeidssokerregistreringRepository arbeidssokerregistreringRepository,
                                     AktorService aktorService,
                                     OppfolgingClient oppfolgingClient,
                                     SykmeldtInfoClient sykmeldtInfoClient,
                                     ArbeidsforholdGateway arbeidsforholdGateway,
                                     ManuellRegistreringService manuellRegistreringService,
                                     StartRegistreringUtils startRegistreringUtils,
                                     RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature

    ) {
        this.arbeidssokerregistreringRepository = arbeidssokerregistreringRepository;
        this.aktorService = aktorService;
        this.sykemeldtRegistreringFeature = sykemeldtRegistreringFeature;
        this.oppfolgingClient = oppfolgingClient;
        this.sykmeldtInfoClient = sykmeldtInfoClient;
        this.arbeidsforholdGateway = arbeidsforholdGateway;
        this.manuellRegistreringService = manuellRegistreringService;
        this.startRegistreringUtils = startRegistreringUtils;
    }

    @Transactional
    public void reaktiverBruker(String fnr) {

        Boolean kanReaktiveres = hentStartRegistreringStatus(fnr).getRegistreringType() == RegistreringType.REAKTIVERING;
        if (!kanReaktiveres) {
            throw new RuntimeException("Bruker kan ikke reaktiveres.");
        }

        AktorId aktorId = getAktorIdOrElseThrow(aktorService, fnr);

        arbeidssokerregistreringRepository.lagreReaktiveringForBruker(aktorId);
        oppfolgingClient.reaktiverBruker(fnr);

        log.info("Reaktivering av bruker med aktørId : {}", aktorId);
    }

    @Transactional
    public OrdinaerBrukerRegistrering registrerBruker(OrdinaerBrukerRegistrering bruker, String fnr) {

        StartRegistreringStatus startRegistreringStatus = hentStartRegistreringStatus(fnr);

        if (startRegistreringStatus.isUnderOppfolging()) {
            throw new RuntimeException("Bruker allerede under oppfølging.");
        }

        if (!ORDINAER_REGISTRERING.equals(startRegistreringStatus.getRegistreringType())) {
            throw new RuntimeException("Brukeren kan ikke registreres. Krever registreringtypen ordinær.");
        }

        try {
            validerBrukerRegistrering(bruker);
        } catch (RuntimeException e) {
            log.warn("Ugyldig innsendt registrering. Besvarelse: {} Stilling: {}", bruker.getBesvarelse(), bruker.getSisteStilling());
            rapporterInvalidRegistrering(bruker);
            throw e;
        }

        Profilering profilering = profilerBrukerTilInnsatsgruppe(fnr, bruker);

        return opprettBruker(fnr, bruker, profilering);
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

    private OrdinaerBrukerRegistrering opprettBruker(String fnr, OrdinaerBrukerRegistrering bruker, Profilering profilering) {
        AktorId aktorId = getAktorIdOrElseThrow(aktorService, fnr);

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = arbeidssokerregistreringRepository.lagreOrdinaerBruker(bruker, aktorId);
        arbeidssokerregistreringRepository.lagreProfilering(ordinaerBrukerRegistrering.getId(), profilering);
        oppfolgingClient.aktiverBruker(new AktiverBrukerData(new Fnr(fnr), profilering.getInnsatsgruppe()));

        rapporterProfilering(profilering);
        rapporterOrdinaerBesvarelse(bruker, profilering);
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

    public BrukerRegistreringWrapper hentBrukerRegistrering(Fnr fnr) {

        AktorId aktorId = getAktorIdOrElseThrow(aktorService, fnr.getFnr());

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = arbeidssokerregistreringRepository
                .hentOrdinaerBrukerregistreringMedProfileringForAktorId(aktorId);

        SykmeldtRegistrering sykmeldtBrukerRegistrering = arbeidssokerregistreringRepository
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
    public long registrerSykmeldt(SykmeldtRegistrering sykmeldtRegistrering, String fnr) {
        if (!sykemeldtRegistreringFeature.erSykemeldtRegistreringAktiv()) {
            throw new RuntimeException("Tjenesten for sykmeldt-registrering er togglet av.");
        }

        ofNullable(sykmeldtRegistrering.getBesvarelse())
                .orElseThrow(() -> new RuntimeException("Besvarelse for sykmeldt ugyldig."));

        StartRegistreringStatus startRegistreringStatus = hentStartRegistreringStatus(fnr);
        long id;

        if (SYKMELDT_REGISTRERING.equals(startRegistreringStatus.getRegistreringType())) {
            AktorId aktorId = getAktorIdOrElseThrow(aktorService, fnr);
            SykmeldtBrukerType sykmeldtBrukerType = startRegistreringUtils.finnSykmeldtBrukerType(sykmeldtRegistrering);
            oppfolgingClient.settOppfolgingSykmeldt(sykmeldtBrukerType, fnr);
            id = arbeidssokerregistreringRepository.lagreSykmeldtBruker(sykmeldtRegistrering, aktorId);
            log.info("Sykmeldtregistrering gjennomført med data {}", sykmeldtRegistrering);
        } else {
            throw new RuntimeException("Bruker kan ikke registreres.");
        }

        return id;
    }

    public SykmeldtInfoData hentSykmeldtInfoData(String fnr) {

        SykmeldtInfoData sykmeldtInfoData = new SykmeldtInfoData();

        if (AutentiseringUtils.erVeileder()) {
            // Veiledere har ikke tilgang til å gjøre kall mot infotrygd
            // Sett inngang aktiv, slik at de får registrert sykmeldte brukere
            sykmeldtInfoData.setErArbeidsrettetOppfolgingSykmeldtInngangAktiv(true);
        } else {
            InfotrygdData infotrygdData = sykmeldtInfoClient.hentSykmeldtInfoData(fnr);
            boolean erSykmeldtOver39Uker = DateUtils.beregnSykmeldtMellom39Og52Uker(infotrygdData.maksDato, now());

            sykmeldtInfoData.setMaksDato(infotrygdData.maksDato);
            sykmeldtInfoData.setErArbeidsrettetOppfolgingSykmeldtInngangAktiv(erSykmeldtOver39Uker);
        }

        return sykmeldtInfoData;
    }
}
