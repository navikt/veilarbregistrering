package no.nav.fo.veilarbregistrering.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.domain.*;
import no.nav.fo.veilarbregistrering.httpclient.OppfolgingClient;
import no.nav.fo.veilarbregistrering.httpclient.DigisyfoClient;
import no.nav.fo.veilarbregistrering.utils.FnrUtils;
import org.springframework.transaction.annotation.Transactional;

import static java.time.LocalDate.now;
import static java.util.Optional.ofNullable;
import static no.nav.fo.veilarbregistrering.domain.RegistreringType.*;
import static no.nav.fo.veilarbregistrering.service.ValideringUtils.validerBrukerRegistrering;
import static no.nav.fo.veilarbregistrering.utils.FnrUtils.utledAlderForFnr;
import static no.nav.fo.veilarbregistrering.utils.FunksjonelleMetrikker.rapporterInvalidRegistrering;
import static no.nav.fo.veilarbregistrering.utils.FunksjonelleMetrikker.rapporterProfilering;


@Slf4j
public class BrukerRegistreringService {

    private final ArbeidssokerregistreringRepository arbeidssokerregistreringRepository;
    private final AktorService aktorService;
    private OppfolgingClient oppfolgingClient;
    private DigisyfoClient sykeforloepMetadataClient;
    private ArbeidsforholdService arbeidsforholdService;
    private StartRegistreringUtilsService startRegistreringUtilsService;

    public BrukerRegistreringService(ArbeidssokerregistreringRepository arbeidssokerregistreringRepository,
                                     AktorService aktorService,
                                     OppfolgingClient oppfolgingClient,
                                     DigisyfoClient sykeforloepMetadataClient,
                                     ArbeidsforholdService arbeidsforholdService,
                                     StartRegistreringUtilsService startRegistreringUtilsService

    ) {
        this.arbeidssokerregistreringRepository = arbeidssokerregistreringRepository;
        this.aktorService = aktorService;
        this.oppfolgingClient = oppfolgingClient;
        this.sykeforloepMetadataClient = sykeforloepMetadataClient;
        this.arbeidsforholdService = arbeidsforholdService;
        this.startRegistreringUtilsService = startRegistreringUtilsService;
    }

    @Transactional
    public void reaktiverBruker(String fnr) {

        Boolean kanReaktiveres = hentStartRegistreringStatus(fnr).getKreverReaktivering();
        if (kanReaktiveres == null || !kanReaktiveres) {
            throw new RuntimeException("Bruker kan ikke reaktiveres.");
        }

        AktorId aktorId = FnrUtils.getAktorIdOrElseThrow(aktorService, fnr);

        arbeidssokerregistreringRepository.lagreReaktiveringForBruker(aktorId);
        oppfolgingClient.reaktiverBruker(fnr);

        log.info("Reaktivering av bruker med aktørId : {}", aktorId);
    }

    @Transactional
    public BrukerRegistrering registrerBruker(BrukerRegistrering bruker, String fnr) {

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

        boolean erSykmeldtMedArbeidsgiverOver39uker = false;
        if (ofNullable(oppfolgingStatusData.erSykmeldtMedArbeidsgiver).orElse(false)) {
            erSykmeldtMedArbeidsgiverOver39uker = hentErSykmeldtOver39uker();
        }

        RegistreringType regStatus = beregnRegistreringType(oppfolgingStatusData, erSykmeldtMedArbeidsgiverOver39uker);

        StartRegistreringStatus startRegistreringStatus = new StartRegistreringStatus()
                .setUnderOppfolging(oppfolgingStatusData.isUnderOppfolging())
                .setKreverReaktivering(oppfolgingStatusData.getKanReaktiveres())
                .setErIkkeArbeidssokerUtenOppfolging(oppfolgingStatusData.getErIkkeArbeidssokerUtenOppfolging())
                .setErSykemeldtMedArbeidsgiverOver39uker(erSykmeldtMedArbeidsgiverOver39uker)
                .setRegistreringType(regStatus);

        if(!oppfolgingStatusData.isUnderOppfolging()) {
            boolean oppfyllerBetingelseOmArbeidserfaring = startRegistreringUtilsService.harJobbetSammenhengendeSeksAvTolvSisteManeder(
                    () -> arbeidsforholdService.hentArbeidsforhold(fnr),
                    now());
            startRegistreringStatus.setJobbetSeksAvTolvSisteManeder(oppfyllerBetingelseOmArbeidserfaring);
        }

        log.info("Returnerer startregistreringsstatus {}", startRegistreringStatus);
        return startRegistreringStatus;
    }

    private RegistreringType beregnRegistreringType(OppfolgingStatusData oppfolgingStatusData, boolean erSykmeldtMedArbeidsgiverOver39uker) {
        if (oppfolgingStatusData.isUnderOppfolging()) {
            return ALLEREDE_REGISTRERT;
        } else if (oppfolgingStatusData.getKanReaktiveres()) {
            return REAKTIVERING;
        } else if (ofNullable(oppfolgingStatusData.erSykmeldtMedArbeidsgiver).orElse(false)
                && erSykmeldtMedArbeidsgiverOver39uker) {
            return SYKMELDT_REGISTRERING;
        } else if (ofNullable(oppfolgingStatusData.erSykmeldtMedArbeidsgiver).orElse(false)
                && !erSykmeldtMedArbeidsgiverOver39uker) {
            return SPERRET;
        } else {
            return ORDINAER_REGISTRERING;
        }
    }

    private BrukerRegistrering opprettBruker(String fnr, BrukerRegistrering bruker, Profilering profilering) {
        AktorId aktorId = FnrUtils.getAktorIdOrElseThrow(aktorService, fnr);

        BrukerRegistrering brukerRegistrering = arbeidssokerregistreringRepository.lagreBruker(bruker, aktorId);
        arbeidssokerregistreringRepository.lagreProfilering(brukerRegistrering.getId(), profilering);
        oppfolgingClient.aktiverBruker(new AktiverBrukerData(new Fnr(fnr), profilering.getInnsatsgruppe()));

        rapporterProfilering(profilering);
        log.info("Brukerregistrering gjennomført med data {}, Profilering {}", brukerRegistrering, profilering);
        return brukerRegistrering;
    }

    public ProfilertBrukerRegistrering hentProfilertBrukerRegistrering(Fnr fnr) {
        return arbeidssokerregistreringRepository.hentProfilertBrukerregistreringForAktorId(
                FnrUtils.getAktorIdOrElseThrow(aktorService, fnr.getFnr())
        );
    }


    private Profilering profilerBrukerTilInnsatsgruppe(String fnr, BrukerRegistrering bruker) {
        return startRegistreringUtilsService.profilerBruker(
                bruker,
                utledAlderForFnr(fnr, now()),
                () -> arbeidsforholdService.hentArbeidsforhold(fnr),
                now());
    }

    public void registrerSykmeldt(String fnr) {
        StartRegistreringStatus startRegistreringStatus = hentStartRegistreringStatus(fnr);
        if (startRegistreringStatus.isErSykemeldtMedArbeidsgiverOver39uker()) {
            oppfolgingClient.settOppfolgingSykmeldt();
            //Lagring
        } else {
            throw new RuntimeException("Registreringsinformasjon er ugyldig");
        }
    }

    private boolean hentErSykmeldtOver39uker() {
        SykeforloepMetaData sykeforloepMetaData = sykeforloepMetadataClient.hentSykeforloepMetadata();
        return ofNullable(sykeforloepMetaData.erArbeidsrettetOppfolgingSykmeldtInngangAktiv).orElse(false);
    }
}
