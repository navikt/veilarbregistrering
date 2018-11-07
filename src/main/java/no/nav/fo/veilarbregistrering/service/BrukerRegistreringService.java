package no.nav.fo.veilarbregistrering.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.domain.*;
import no.nav.fo.veilarbregistrering.httpclient.DigisyfoClient;
import no.nav.fo.veilarbregistrering.httpclient.OppfolgingClient;
import no.nav.fo.veilarbregistrering.utils.FnrUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static java.time.LocalDate.now;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static no.nav.fo.veilarbregistrering.domain.RegistreringType.SYKMELDT_REGISTRERING;
import static no.nav.fo.veilarbregistrering.service.StartRegistreringUtils.beregnRegistreringType;
import static no.nav.fo.veilarbregistrering.service.ValideringUtils.validerBrukerRegistrering;
import static no.nav.fo.veilarbregistrering.utils.FnrUtils.utledAlderForFnr;
import static no.nav.fo.veilarbregistrering.utils.FunksjonelleMetrikker.rapporterInvalidRegistrering;
import static no.nav.fo.veilarbregistrering.utils.FunksjonelleMetrikker.rapporterProfilering;


@Slf4j
public class BrukerRegistreringService {

    private final ArbeidssokerregistreringRepository arbeidssokerregistreringRepository;
    private final AktorService aktorService;
    private final RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature;
    private OppfolgingClient oppfolgingClient;
    private DigisyfoClient sykeforloepMetadataClient;
    private ArbeidsforholdService arbeidsforholdService;
    private StartRegistreringUtils startRegistreringUtils;

    public BrukerRegistreringService(ArbeidssokerregistreringRepository arbeidssokerregistreringRepository,
                                     AktorService aktorService,
                                     OppfolgingClient oppfolgingClient,
                                     DigisyfoClient sykeforloepMetadataClient,
                                     ArbeidsforholdService arbeidsforholdService,
                                     StartRegistreringUtils startRegistreringUtils,
                                     RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature

    ) {
        this.arbeidssokerregistreringRepository = arbeidssokerregistreringRepository;
        this.aktorService = aktorService;
        this.sykemeldtRegistreringFeature = sykemeldtRegistreringFeature;
        this.oppfolgingClient = oppfolgingClient;
        this.sykeforloepMetadataClient = sykeforloepMetadataClient;
        this.arbeidsforholdService = arbeidsforholdService;
        this.startRegistreringUtils = startRegistreringUtils;
    }

    @Transactional
    public void reaktiverBruker(String fnr) {

        Boolean kanReaktiveres = hentStartRegistreringStatus(fnr).getRegistreringType() == RegistreringType.REAKTIVERING;
        if (!kanReaktiveres) {
            throw new RuntimeException("Bruker kan ikke reaktiveres.");
        }

        AktorId aktorId = FnrUtils.getAktorIdOrElseThrow(aktorService, fnr);

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

        Optional<SykeforloepMetaData> sykeforloepMetaData = empty();
        if (ofNullable(oppfolgingStatusData.erSykmeldtMedArbeidsgiver).orElse(false)) {
            sykeforloepMetaData = hentSykeforloepMetaData();
        }

        RegistreringType registreringType = beregnRegistreringType(oppfolgingStatusData, sykeforloepMetaData);

        StartRegistreringStatus startRegistreringStatus = new StartRegistreringStatus()
                .setUnderOppfolging(oppfolgingStatusData.isUnderOppfolging())
                .setSykmeldtFraDato(sykeforloepMetaData.map(s -> s.getSykmeldtFraDato()).orElse(""))
                .setRegistreringType(registreringType);

        if(!oppfolgingStatusData.isUnderOppfolging()) {
            boolean oppfyllerBetingelseOmArbeidserfaring = startRegistreringUtils.harJobbetSammenhengendeSeksAvTolvSisteManeder(
                    () -> arbeidsforholdService.hentArbeidsforhold(fnr),
                    now());
            startRegistreringStatus.setJobbetSeksAvTolvSisteManeder(oppfyllerBetingelseOmArbeidserfaring);
        }

        log.info("Returnerer startregistreringsstatus {}", startRegistreringStatus);
        return startRegistreringStatus;
    }

    private OrdinaerBrukerRegistrering opprettBruker(String fnr, OrdinaerBrukerRegistrering bruker, Profilering profilering) {
        AktorId aktorId = FnrUtils.getAktorIdOrElseThrow(aktorService, fnr);

        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = arbeidssokerregistreringRepository.lagreBruker(bruker, aktorId);
        arbeidssokerregistreringRepository.lagreProfilering(ordinaerBrukerRegistrering.getId(), profilering);
        oppfolgingClient.aktiverBruker(new AktiverBrukerData(new Fnr(fnr), profilering.getInnsatsgruppe()));

        rapporterProfilering(profilering);
        log.info("Brukerregistrering gjennomført med data {}, Profilering {}", ordinaerBrukerRegistrering, profilering);
        return ordinaerBrukerRegistrering;
    }

    public ProfilertBrukerRegistrering hentProfilertBrukerRegistrering(Fnr fnr) {
        return arbeidssokerregistreringRepository.hentProfilertBrukerregistreringForAktorId(
                FnrUtils.getAktorIdOrElseThrow(aktorService, fnr.getFnr())
        );
    }

    private Profilering profilerBrukerTilInnsatsgruppe(String fnr, OrdinaerBrukerRegistrering bruker) {
        return startRegistreringUtils.profilerBruker(
                bruker,
                utledAlderForFnr(fnr, now()),
                () -> arbeidsforholdService.hentArbeidsforhold(fnr),
                now());
    }

    public void registrerSykmeldt(String fnr) {
        if (!sykemeldtRegistreringFeature.erSykemeldtRegistreringAktiv()) {
            throw new RuntimeException("Tjenesten er togglet av.");
        }
        StartRegistreringStatus startRegistreringStatus = hentStartRegistreringStatus(fnr);
        if (SYKMELDT_REGISTRERING.equals(startRegistreringStatus.getRegistreringType())) {
            oppfolgingClient.settOppfolgingSykmeldt();
            //Lagring
        } else {
            throw new RuntimeException("Registreringsinformasjon er ugyldig");
        }
    }

    private Optional<SykeforloepMetaData> hentSykeforloepMetaData() {
        if (sykemeldtRegistreringFeature.skalMockeDataFraDigisyfo()) {
            //Mocker data fra Digisyfo. todo: må fjernes når Digisyfo-tjenesten er tilgjengelig i prod.
            return of(new SykeforloepMetaData()
                    .withErArbeidsrettetOppfolgingSykmeldtInngangAktiv(true)
                    .withSykmeldtFraDato("2018-01-21"));
        }

        if (sykemeldtRegistreringFeature.skalKalleDigisyfoTjeneste()) {
            return of(sykeforloepMetadataClient.hentSykeforloepMetadata());
        } else {
            return empty();
        }
    }
}
