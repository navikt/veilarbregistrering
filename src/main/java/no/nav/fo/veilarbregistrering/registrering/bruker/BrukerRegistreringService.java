package no.nav.fo.veilarbregistrering.registrering.bruker;

import lombok.extern.slf4j.Slf4j;
import no.nav.apiapp.security.veilarbabac.Bruker;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.*;
import no.nav.fo.veilarbregistrering.profilering.Profilering;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.StartRegistreringUtils;
import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.registrering.bruker.besvarelse.FremtidigSituasjonSvar;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

import static java.time.LocalDate.now;
import static java.util.Optional.ofNullable;
import static no.nav.fo.veilarbregistrering.oppfolging.adapter.SykmeldtBrukerType.SKAL_TIL_NY_ARBEIDSGIVER;
import static no.nav.fo.veilarbregistrering.oppfolging.adapter.SykmeldtBrukerType.SKAL_TIL_SAMME_ARBEIDSGIVER;
import static no.nav.fo.veilarbregistrering.registrering.bruker.FnrUtils.utledAlderForFnr;
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.*;
import static no.nav.fo.veilarbregistrering.registrering.bruker.ValideringUtils.validerBrukerRegistrering;
import static no.nav.fo.veilarbregistrering.utils.FunksjonelleMetrikker.*;


@Slf4j
public class BrukerRegistreringService {

    private final BrukerRegistreringRepository brukerRegistreringRepository;
    private final ProfileringRepository profileringRepository;
    private final RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature;
    private final SykemeldingService sykemeldingService;
    private OppfolgingGateway oppfolgingGateway;
    private ArbeidsforholdGateway arbeidsforholdGateway;
    private ManuellRegistreringService manuellRegistreringService;
    private StartRegistreringUtils startRegistreringUtils;

    public BrukerRegistreringService(BrukerRegistreringRepository brukerRegistreringRepository,
                                     ProfileringRepository profileringRepository,
                                     OppfolgingGateway oppfolgingGateway,
                                     SykemeldingService sykemeldingService,
                                     ArbeidsforholdGateway arbeidsforholdGateway,
                                     ManuellRegistreringService manuellRegistreringService,
                                     StartRegistreringUtils startRegistreringUtils,
                                     RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature

    ) {
        this.brukerRegistreringRepository = brukerRegistreringRepository;
        this.profileringRepository = profileringRepository;
        this.sykemeldtRegistreringFeature = sykemeldtRegistreringFeature;
        this.oppfolgingGateway = oppfolgingGateway;
        this.sykemeldingService = sykemeldingService;
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
        oppfolgingGateway.reaktiverBruker(bruker.getFoedselsnummer());

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

        return opprettBruker(bruker, ordinaerBrukerRegistrering);
    }

    public StartRegistreringStatus hentStartRegistreringStatus(String fnr) {
        Oppfolgingsstatus oppfolgingStatusData = oppfolgingGateway.hentOppfolgingsstatus(fnr);

        SykmeldtInfoData sykeforloepMetaData = null;
        String maksDato = "";
        boolean erSykmeldtMedArbeidsgiver = ofNullable(oppfolgingStatusData.getErSykmeldtMedArbeidsgiver()).orElse(false);
        if (erSykmeldtMedArbeidsgiver) {
            if (sykemeldtRegistreringFeature.erSykemeldtRegistreringAktiv()) {
                sykeforloepMetaData = sykemeldingService.hentSykmeldtInfoData(fnr);
                maksDato = sykeforloepMetaData.maksDato;
            }
        }

        RegistreringType registreringType = beregnRegistreringType(oppfolgingStatusData, sykeforloepMetaData);

        StartRegistreringStatus startRegistreringStatus = new StartRegistreringStatus()
                .setUnderOppfolging(oppfolgingStatusData.isUnderOppfolging())
                .setRegistreringType(registreringType)
                .setErSykmeldtMedArbeidsgiver(erSykmeldtMedArbeidsgiver)
                .setMaksDato(maksDato)
                .setFormidlingsgruppe(oppfolgingStatusData.getFormidlingsgruppe())
                .setServicegruppe(oppfolgingStatusData.getServicegruppe());

        if (ORDINAER_REGISTRERING.equals(registreringType)) {
            boolean oppfyllerBetingelseOmArbeidserfaring = startRegistreringUtils.harJobbetSammenhengendeSeksAvTolvSisteManeder(
                    () -> arbeidsforholdGateway.hentArbeidsforhold(fnr),
                    now());
            startRegistreringStatus.setJobbetSeksAvTolvSisteManeder(oppfyllerBetingelseOmArbeidserfaring);
        }

        log.info("Returnerer startregistreringsstatus {}", startRegistreringStatus);
        return startRegistreringStatus;
    }

    private OrdinaerBrukerRegistrering opprettBruker(Bruker bruker, OrdinaerBrukerRegistrering brukerRegistrering) {
        AktorId aktorId = new AktorId(bruker.getAktoerId());

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = brukerRegistreringRepository.lagreOrdinaerBruker(brukerRegistrering, aktorId);

        Profilering profilering = profilerBrukerTilInnsatsgruppe(bruker.getFoedselsnummer(), ordinaerBrukerRegistrering.getBesvarelse());
        profileringRepository.lagreProfilering(ordinaerBrukerRegistrering.getId(), profilering);

        oppfolgingGateway.aktiverBruker(bruker.getFoedselsnummer(), profilering.getInnsatsgruppe());

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

        StartRegistreringStatus startRegistreringStatus = hentStartRegistreringStatus(bruker.getFoedselsnummer());

        if (!SYKMELDT_REGISTRERING.equals(startRegistreringStatus.getRegistreringType())) {
            throw new RuntimeException("Bruker kan ikke registreres.");
        }

        oppfolgingGateway.settOppfolgingSykmeldt(bruker.getFoedselsnummer(), sykmeldtRegistrering.getBesvarelse());
        AktorId aktorId = new AktorId(bruker.getAktoerId());
        long id = brukerRegistreringRepository.lagreSykmeldtBruker(sykmeldtRegistrering, aktorId);
        log.info("Sykmeldtregistrering gjennomført med data {}", sykmeldtRegistrering);

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

}
