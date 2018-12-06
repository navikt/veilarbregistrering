package no.nav.fo.veilarbregistrering.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.domain.*;
import no.nav.fo.veilarbregistrering.httpclient.OppfolgingClient;
import no.nav.fo.veilarbregistrering.httpclient.SykmeldtInfoClient;
import no.nav.fo.veilarbregistrering.utils.DateUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static java.time.LocalDate.now;
import static java.util.Optional.ofNullable;
import static no.nav.fo.veilarbregistrering.domain.RegistreringType.ORDINAER_REGISTRERING;
import static no.nav.fo.veilarbregistrering.domain.RegistreringType.SYKMELDT_REGISTRERING;
import static no.nav.fo.veilarbregistrering.service.StartRegistreringUtils.beregnRegistreringType;
import static no.nav.fo.veilarbregistrering.service.ValideringUtils.validerBrukerRegistrering;
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
    private ArbeidsforholdService arbeidsforholdService;
    private StartRegistreringUtils startRegistreringUtils;

    public BrukerRegistreringService(ArbeidssokerregistreringRepository arbeidssokerregistreringRepository,
                                     AktorService aktorService,
                                     OppfolgingClient oppfolgingClient,
                                     SykmeldtInfoClient sykmeldtInfoClient,
                                     ArbeidsforholdService arbeidsforholdService,
                                     StartRegistreringUtils startRegistreringUtils,
                                     RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature

    ) {
        this.arbeidssokerregistreringRepository = arbeidssokerregistreringRepository;
        this.aktorService = aktorService;
        this.sykemeldtRegistreringFeature = sykemeldtRegistreringFeature;
        this.oppfolgingClient = oppfolgingClient;
        this.sykmeldtInfoClient = sykmeldtInfoClient;
        this.arbeidsforholdService = arbeidsforholdService;
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
        if (ofNullable(oppfolgingStatusData.erSykmeldtMedArbeidsgiver).orElse(false)) {
            if (sykemeldtRegistreringFeature.erSykemeldtRegistreringAktiv()) {
                sykeforloepMetaData = hentSykmeldtInfoData(fnr);
            }
        }

        RegistreringType registreringType = beregnRegistreringType(oppfolgingStatusData, sykeforloepMetaData);

        StartRegistreringStatus startRegistreringStatus = new StartRegistreringStatus()
                .setUnderOppfolging(oppfolgingStatusData.isUnderOppfolging())
                .setRegistreringType(registreringType);

        if (ORDINAER_REGISTRERING.equals(registreringType)) {
            boolean oppfyllerBetingelseOmArbeidserfaring = startRegistreringUtils.harJobbetSammenhengendeSeksAvTolvSisteManeder(
                    () -> arbeidsforholdService.hentArbeidsforhold(fnr),
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

    public BrukerRegistreringWrapper hentBrukerRegistrering(Fnr fnr) {

        AktorId aktorId = getAktorIdOrElseThrow(aktorService, fnr.getFnr());

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = arbeidssokerregistreringRepository
                .hentOrdinaerBrukerregistreringMedProfileringForAktorId(aktorId);

        SykmeldtRegistrering sykmeldtBrukerRegistrering = arbeidssokerregistreringRepository
                .hentSykmeldtregistreringForAktorId(aktorId);

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
                () -> arbeidsforholdService.hentArbeidsforhold(fnr),
                now());
    }

    @Transactional
    public void registrerSykmeldt(SykmeldtRegistrering sykmeldtRegistrering, String fnr) {
        if (!sykemeldtRegistreringFeature.erSykemeldtRegistreringAktiv()) {
            throw new RuntimeException("Tjenesten for sykmeldt-registrering er togglet av.");
        }

        ofNullable(sykmeldtRegistrering.getBesvarelse())
                .orElseThrow(() -> new RuntimeException("Besvarelse for sykmeldt ugyldig."));

        StartRegistreringStatus startRegistreringStatus = hentStartRegistreringStatus(fnr);
        if (SYKMELDT_REGISTRERING.equals(startRegistreringStatus.getRegistreringType())) {
            AktorId aktorId = getAktorIdOrElseThrow(aktorService, fnr);
            SykmeldtBrukerType sykmeldtBrukerType = startRegistreringUtils.finnSykmeldtBrukerType(sykmeldtRegistrering);

            oppfolgingClient.settOppfolgingSykmeldt(sykmeldtBrukerType);
            arbeidssokerregistreringRepository.lagreSykmeldtBruker(sykmeldtRegistrering, aktorId);
            log.info("Sykmeldtregistrering gjennomført med data {}", sykmeldtRegistrering);
        } else {
            throw new RuntimeException("Bruker kan ikke registreres.");
        }
    }

    public SykmeldtInfoData hentSykmeldtInfoData(String fnr) {

        if (!sykemeldtRegistreringFeature.skalKalleInfoTrygdTjeneste()) {
            SykmeldtInfoData sykmeldtInfoData = new SykmeldtInfoData();
            sykmeldtInfoData.setMaksDato("");
            sykmeldtInfoData.setErArbeidsrettetOppfolgingSykmeldtInngangAktiv(false);
            return sykmeldtInfoData;
        }

        InfotrygdData infotrygdData = sykmeldtInfoClient.hentSykmeldtInfoData(fnr);

        boolean erSykmeldtOver39Uker = DateUtils.beregnSykmeldtOver39uker(infotrygdData.maksDato, now());

        SykmeldtInfoData sykmeldtInfoData = new SykmeldtInfoData();
        sykmeldtInfoData.setMaksDato(infotrygdData.maksDato);
        sykmeldtInfoData.setErArbeidsrettetOppfolgingSykmeldtInngangAktiv(false);

        if (erSykmeldtOver39Uker) {
            sykmeldtInfoData.setErArbeidsrettetOppfolgingSykmeldtInngangAktiv(true);
        }

        return sykmeldtInfoData;
    }
}
