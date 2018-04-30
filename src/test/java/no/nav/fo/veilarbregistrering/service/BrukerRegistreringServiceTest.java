package no.nav.fo.veilarbregistrering.service;

import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.domain.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.OppfolgingStatus;
import no.nav.fo.veilarbregistrering.domain.StartRegistreringStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static no.nav.fo.veilarbregistrering.service.Konstanter.*;
import static no.nav.fo.veilarbregistrering.utils.StartRegistreringUtils.MAX_ALDER_AUTOMATISK_REGISTRERING;
import static no.nav.fo.veilarbregistrering.utils.StartRegistreringUtils.MIN_ALDER_AUTOMATISK_REGISTRERING;
import static no.nav.fo.veilarbregistrering.utils.TestUtils.getFodselsnummerForPersonWithAge;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class BrukerRegistreringServiceTest {

    private static String FNR_OPPFYLLER_KRAV = getFodselsnummerForPersonWithAge(40);
    private static String FNR_OPPFYLLER_IKKE_KRAV = getFodselsnummerForPersonWithAge(20);

    private ArbeidssokerregistreringRepository arbeidssokerregistreringRepository;
    private AktorService aktorService;
    private BrukerRegistreringService brukerRegistreringService;
    private OppfolgingService oppfolgingService;
    private ArbeidsforholdService arbeidsforholdService;
    private RemoteFeatureConfig.OpprettBrukerIArenaFeature opprettBrukerIArenaFeature;
    private RemoteFeatureConfig.RegistreringFeature registreringFeature;


    @BeforeEach
    public void setup() {
        opprettBrukerIArenaFeature = mock(RemoteFeatureConfig.OpprettBrukerIArenaFeature.class);
        registreringFeature = mock(RemoteFeatureConfig.RegistreringFeature.class);
        aktorService = mock(AktorService.class);
        arbeidssokerregistreringRepository = mock(ArbeidssokerregistreringRepository.class);
        oppfolgingService = mock(OppfolgingService.class);
        arbeidsforholdService = mock(ArbeidsforholdService.class);

        System.setProperty(MIN_ALDER_AUTOMATISK_REGISTRERING, "30");
        System.setProperty(MAX_ALDER_AUTOMATISK_REGISTRERING, "59");

        brukerRegistreringService =
                new BrukerRegistreringService(
                        arbeidssokerregistreringRepository,
                        aktorService,
                        opprettBrukerIArenaFeature,
                        registreringFeature,
                        oppfolgingService,
                        arbeidsforholdService);

        when(aktorService.getAktorId(any())).thenReturn(Optional.of("AKTORID"));
        when(opprettBrukerIArenaFeature.erAktiv()).thenReturn(true);
        when(registreringFeature.erAktiv()).thenReturn(true);
    }

    /*
     * Test av besvarelsene og lagring
     * */
    @Test
    void skalRegistrereSelvgaaendeBruker() throws Exception {
        mockInaktivBruker();
        mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker();
        BrukerRegistrering selvgaaendeBruker = getBrukerRegistreringSelvgaaende();
        registrerBruker(selvgaaendeBruker, FNR_OPPFYLLER_KRAV);
        verify(arbeidssokerregistreringRepository, times(1)).lagreBruker(any(), any());
    }

    @Test
    void skalRegistrereSelvgaaendeBrukerIDatabasenSelvOmArenaErToggletBort() throws Exception {
        when(opprettBrukerIArenaFeature.erAktiv()).thenReturn(false);
        mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker();
        BrukerRegistrering selvgaaendeBruker = getBrukerRegistreringSelvgaaende();
        registrerBruker(selvgaaendeBruker, FNR_OPPFYLLER_KRAV);
        verify(oppfolgingService, times(0)).aktiverBruker(any());
        verify(arbeidssokerregistreringRepository, times(1)).lagreBruker(any(), any());
    }

    @Test
    void skalRegistrereIArenaNaarArenaToggleErPaa() throws Exception {
        when(opprettBrukerIArenaFeature.erAktiv()).thenReturn(true);
        mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker();
        registrerBruker(getBrukerRegistreringSelvgaaende(), FNR_OPPFYLLER_KRAV);
        verify(oppfolgingService, times(1)).aktiverBruker(any());
    }

    @Test
    void skalKasteRuntimeExceptionDersomRegistreringFeatureErAv() throws Exception {
        when(registreringFeature.erAktiv()).thenReturn(false);
        mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker();
        assertThrows(RuntimeException.class, () -> registrerBruker(getBrukerRegistreringSelvgaaende(), FNR_OPPFYLLER_KRAV));
        verify(oppfolgingService, times(0)).aktiverBruker(any());
    }

    @Test
    void skalIkkeLagreRegistreringSomErUnderOppfolging() {
        mockBrukerUnderOppfolging();
        BrukerRegistrering selvgaaendeBruker = getBrukerRegistreringSelvgaaende();
        assertThrows(RuntimeException.class, () -> registrerBruker(selvgaaendeBruker, FNR_OPPFYLLER_KRAV));
    }

    @Test
    void skalIkkeLagreRegistreringSomIkkeOppfyllerKravForAutomatiskRegistrering() throws Exception {
        mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker();
        BrukerRegistrering selvgaaendeBruker = getBrukerIngenUtdannelse();
        assertThrows(RuntimeException.class, () -> registrerBruker(selvgaaendeBruker, FNR_OPPFYLLER_IKKE_KRAV));
    }

    @Test
    void skalIkkeLagreRegistreringDersomIngenUtdannelse() throws Exception {
        mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker();
        BrukerRegistrering ikkeSelvgaaendeBruker = getBrukerIngenUtdannelse();
        assertThrows(RuntimeException.class, () -> registrerBruker(ikkeSelvgaaendeBruker, FNR_OPPFYLLER_KRAV));
    }

    @Test
    void skalIkkeLagreRegistreringMedHelseutfordringer() throws Exception {
        mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker();
        BrukerRegistrering brukerRegistreringMedHelseutfordringer = getBrukerRegistreringMedHelseutfordringer();
        assertThrows(RuntimeException.class, () -> registrerBruker(brukerRegistreringMedHelseutfordringer, FNR_OPPFYLLER_KRAV));
    }

    @Test
    public void skalReturnerUnderOppfolgingNaarUnderOppfolgingIArena() {
        mockArbeidssokerSomOppfyllerKravFraArena();
        StartRegistreringStatus startRegistreringStatus = brukerRegistreringService.hentStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        assertThat(startRegistreringStatus.isUnderOppfolging()).isTrue();
    }

    /*
     * Test av kall registrering arena og lagring
     * */
    @Test
    void brukerSomIkkeFinnesIArenaSkalMappesTilNotFoundException() throws Exception {
        mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker();
        //doThrow(mock(AktiverBrukerBrukerFinnesIkke.class)).when(oppfolgingService).aktiverBruker(any());
    //    assertThrows(NotFoundException.class, () -> registrerBruker(getBrukerRegistreringSelvgaaende(), FNR_OPPFYLLER_KRAV));
    }

    @Test
    void brukerSomIkkeKanReaktiveresIArenaSkalGiServerErrorException() throws Exception {
        mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker();
      //  doThrow(mock(AktiverBrukerBrukerIkkeReaktivert.class)).when(behandleArbeidssoekerV1).aktiverBruker(any());
//        assertThrows(ServerErrorException.class, () -> registrerBruker(getBrukerRegistreringSelvgaaende(), FNR_OPPFYLLER_KRAV));
    }

    @Test
    void brukerSomIkkeKanAktiveresIArenaSkalGiServerErrorException() throws Exception {
        mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker();
      //  doThrow(mock(AktiverBrukerBrukerKanIkkeAktiveres.class)).when(behandleArbeidssoekerV1).aktiverBruker(any());
//        assertThrows(ServerErrorException.class, () -> registrerBruker(getBrukerRegistreringSelvgaaende(), FNR_OPPFYLLER_KRAV));
    }

    @Test
    void brukerSomManglerArbeidstillatelseSkalGiServerErrorException() throws Exception {
        mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker();
      //  doThrow(mock(AktiverBrukerBrukerManglerArbeidstillatelse.class)).when(behandleArbeidssoekerV1).aktiverBruker(any());
      //  assertThrows(ServerErrorException.class, () -> registrerBruker(getBrukerRegistreringSelvgaaende(), FNR_OPPFYLLER_KRAV));
    }

    @Test
    void brukerSomIkkeHarTilgangSkalGiNotAuthorizedException() throws Exception {
        mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker();
      //  doThrow(mock(AktiverBrukerSikkerhetsbegrensning.class)).when(behandleArbeidssoekerV1).aktiverBruker(any());
     //   assertThrows(NotAuthorizedException.class, () -> registrerBruker(getBrukerRegistreringSelvgaaende(), FNR_OPPFYLLER_KRAV));
    }

    @Test
    void ugyldigInputSkalGiBadRequestException() throws Exception {
        mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker();

      //  doThrow(mock(AktiverBrukerUgyldigInput.class)).when(behandleArbeidssoekerV1).aktiverBruker(any());
      //  assertThrows(BadRequestException.class, () -> registrerBruker(getBrukerRegistreringSelvgaaende(), FNR_OPPFYLLER_KRAV));
    }

    /*
     * Mock og hjelpe funksjoner
     * */
    static BrukerRegistrering getBrukerRegistreringSelvgaaende() {
        return BrukerRegistrering.builder()
                .nusKode(NUS_KODE_4)
                .yrkesPraksis("1111.11")
                .opprettetDato(null)
                .enigIOppsummering(ENIG_I_OPPSUMMERING)
                .oppsummering(OPPSUMMERING)
                .harHelseutfordringer(HAR_INGEN_HELSEUTFORDRINGER)
                .build();
    }

    private BrukerRegistrering getBrukerIngenUtdannelse() {
        return BrukerRegistrering.builder()
                .nusKode(NUS_KODE_0)
                .yrkesPraksis(null)
                .opprettetDato(null)
                .enigIOppsummering(ENIG_I_OPPSUMMERING)
                .oppsummering(OPPSUMMERING)
                .harHelseutfordringer(HAR_INGEN_HELSEUTFORDRINGER)
                .build();
    }

    private BrukerRegistrering getBrukerRegistreringMedHelseutfordringer() {
        return BrukerRegistrering.builder()
                .nusKode(NUS_KODE_4)
                .yrkesPraksis(null)
                .opprettetDato(null)
                .enigIOppsummering(ENIG_I_OPPSUMMERING)
                .oppsummering(OPPSUMMERING)
                .harHelseutfordringer(HAR_HELSEUTFORDRINGER)
                .build();
    }


    private BrukerRegistrering registrerBruker(BrukerRegistrering bruker, String fnr) {// throws RegistrerBrukerSikkerhetsbegrensning, HentStartRegistreringStatusFeilVedHentingAvStatusFraArena, HentStartRegistreringStatusFeilVedHentingAvArbeidsforhold {
        return brukerRegistreringService.registrerBruker(bruker, fnr);
    }

    private void mockBrukerUnderOppfolging() {
        when(arbeidssokerregistreringRepository.lagreBruker(any(), any())).thenReturn(getBrukerRegistreringSelvgaaende());

    }

    private void mockArbeidssokerSomOppfyllerKravFraArena() {
        when(oppfolgingService.hentOppfolgingsstatusOgFlagg(any())).thenReturn(
                Optional.of(OppfolgingStatus.builder()
                        .servicegruppe("BATT")
                        .formidlingsgruppe("ARBS")
                        .oppfolgingsFlaggFO(false)
                        .build())
        );
    }


    private void mockInaktivBruker() {
        when(oppfolgingService.hentOppfolgingsstatusOgFlagg(any())).thenReturn(
                Optional.of(OppfolgingStatus.builder()
                        .formidlingsgruppe("ISERV")
                        .oppfolgingsFlaggFO(false)
                        .build())
        );
    }

    private void mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker() {
        when(arbeidsforholdService.hentArbeidsforhold(any())).thenReturn(
                Collections.singletonList(new Arbeidsforhold()
                        .setArbeidsgiverOrgnummer("orgnummer")
                        .setStyrk("styrk")
                        .setFom(LocalDate.of(2017,1,10)))
        );
    }
}