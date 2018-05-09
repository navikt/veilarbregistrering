package no.nav.fo.veilarbregistrering.service;

import lombok.SneakyThrows;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.domain.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.domain.BrukerRegistrering;
import no.nav.fo.veilarbregistrering.domain.OppfolgingStatus;
import no.nav.fo.veilarbregistrering.domain.StartRegistreringStatus;
import no.nav.fo.veilarbregistrering.httpclient.OppfolgingClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static java.util.Optional.of;
import static no.nav.fo.veilarbregistrering.service.Konstanter.*;
import static no.nav.fo.veilarbregistrering.service.StartRegistreringUtilsService.MAX_ALDER_AUTOMATISK_REGISTRERING;
import static no.nav.fo.veilarbregistrering.service.StartRegistreringUtilsService.MIN_ALDER_AUTOMATISK_REGISTRERING;
import static no.nav.fo.veilarbregistrering.utils.TestUtils.getFodselsnummerForPersonWithAge;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BrukerRegistreringServiceTest {

    private static String FNR_OPPFYLLER_KRAV = getFodselsnummerForPersonWithAge(40);
    private static String FNR_OPPFYLLER_IKKE_KRAV = getFodselsnummerForPersonWithAge(20);

    private ArbeidssokerregistreringRepository arbeidssokerregistreringRepository;
    private AktorService aktorService;
    private BrukerRegistreringService brukerRegistreringService;
    private OppfolgingClient oppfolgingClient;
    private ArbeidsforholdService arbeidsforholdService;
    private RemoteFeatureConfig.RegistreringFeature registreringFeature;
    private StartRegistreringUtilsService startRegistreringUtilsService;


    @BeforeEach
    public void setup() {
        registreringFeature = mock(RemoteFeatureConfig.RegistreringFeature.class);
        aktorService = mock(AktorService.class);
        arbeidssokerregistreringRepository = mock(ArbeidssokerregistreringRepository.class);
        oppfolgingClient = mock(OppfolgingClient.class);
        arbeidsforholdService = mock(ArbeidsforholdService.class);
        startRegistreringUtilsService = new StartRegistreringUtilsService();

        System.setProperty(MIN_ALDER_AUTOMATISK_REGISTRERING, "30");
        System.setProperty(MAX_ALDER_AUTOMATISK_REGISTRERING, "59");

        brukerRegistreringService =
                new BrukerRegistreringService(
                        arbeidssokerregistreringRepository,
                        aktorService,
                        registreringFeature,
                        oppfolgingClient,
                        arbeidsforholdService,
                        startRegistreringUtilsService);

        when(aktorService.getAktorId(any())).thenReturn(of("AKTORID"));
        when(registreringFeature.erAktiv()).thenReturn(true);
    }

    /*
     * Test av besvarelsene og lagring
     * */
    @Test
    void skalRegistrereSelvgaaendeBruker()  {
        mockInaktivBruker();
        mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker();
        BrukerRegistrering selvgaaendeBruker = getBrukerRegistreringSelvgaaende();
        registrerBruker(selvgaaendeBruker, FNR_OPPFYLLER_KRAV);
        verify(arbeidssokerregistreringRepository, times(1)).lagreBruker(any(), any());
    }

    @Test
    void skalRegistrereSelvgaaendeBrukerIDatabasen()  {
        mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker();
        BrukerRegistrering selvgaaendeBruker = getBrukerRegistreringSelvgaaende();
        registrerBruker(selvgaaendeBruker, FNR_OPPFYLLER_KRAV);
        verify(oppfolgingClient, times(1)).aktiverBruker(any());
        verify(arbeidssokerregistreringRepository, times(1)).lagreBruker(any(), any());
    }

    @Test
    void skalKasteRuntimeExceptionDersomRegistreringFeatureErAv()  {
        when(registreringFeature.erAktiv()).thenReturn(false);
        mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker();
        assertThrows(RuntimeException.class, () -> registrerBruker(getBrukerRegistreringSelvgaaende(), FNR_OPPFYLLER_KRAV));
        verify(oppfolgingClient, times(0)).aktiverBruker(any());
    }

    @Test
    void skalIkkeLagreRegistreringSomErUnderOppfolging() {
        mockBrukerUnderOppfolging();
        BrukerRegistrering selvgaaendeBruker = getBrukerRegistreringSelvgaaende();
        assertThrows(RuntimeException.class, () -> registrerBruker(selvgaaendeBruker, FNR_OPPFYLLER_KRAV));
    }

    @Test
    void skalIkkeLagreRegistreringSomIkkeOppfyllerKravForAutomatiskRegistrering()  {
        mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker();
        BrukerRegistrering selvgaaendeBruker = getBrukerIngenUtdannelse();
        assertThrows(RuntimeException.class, () -> registrerBruker(selvgaaendeBruker, FNR_OPPFYLLER_IKKE_KRAV));
    }

    @Test
    void skalIkkeLagreRegistreringDersomIngenUtdannelse()  {
        mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker();
        BrukerRegistrering ikkeSelvgaaendeBruker = getBrukerIngenUtdannelse();
        assertThrows(RuntimeException.class, () -> registrerBruker(ikkeSelvgaaendeBruker, FNR_OPPFYLLER_KRAV));
    }

    @Test
    void skalIkkeLagreRegistreringMedHelseutfordringer() {
        mockArbeidssforholdSomOppfyllerKravForSelvgaaendeBruker();
        BrukerRegistrering brukerRegistreringMedHelseutfordringer = getBrukerRegistreringMedHelseutfordringer();
        assertThrows(RuntimeException.class, () -> registrerBruker(brukerRegistreringMedHelseutfordringer, FNR_OPPFYLLER_KRAV));
    }

    @Test
    public void skalReturnerUnderOppfolgingNaarUnderOppfolging() {
        mockArbeidssokerSomHarAktivOppfolging();
        StartRegistreringStatus startRegistreringStatus = brukerRegistreringService.hentStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        assertThat(startRegistreringStatus.isUnderOppfolging()).isTrue();
    }

    @Test
    public void skalIkkeOppfylleKravPgaAlder() {
        mockOppfolgingMedRespons(inaktivBrukerMedInaktiveringsDato(LocalDate.now().minusYears(2)));
        mockArbeidsforhold(arbeidsforholdSomOppfyllerKrav());
        StartRegistreringStatus startRegistreringStatus = getStartRegistreringStatus(FNR_OPPFYLLER_IKKE_KRAV);
        assertThat(startRegistreringStatus.isOppfyllerKravForAutomatiskRegistrering()).isFalse();
    }

    @Test
    public void skalIkkeOppfylleKravPgaInaktivDato() {
        mockOppfolgingMedRespons(inaktivBrukerMedInaktiveringsDato(LocalDate.now().minusYears(1)));
        mockArbeidsforhold(arbeidsforholdSomOppfyllerKrav());
        StartRegistreringStatus startRegistreringStatus = getStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        assertThat(startRegistreringStatus.isOppfyllerKravForAutomatiskRegistrering()).isFalse();
    }

    @Test
    public void skalIkkeOppfylleKravPgaArbeidserfaring() {
        mockOppfolgingMedRespons(inaktivBrukerMedInaktiveringsDato(LocalDate.now().minusYears(2)));
        mockArbeidsforhold(Collections.emptyList());
        StartRegistreringStatus startRegistreringStatus = getStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        assertThat(startRegistreringStatus.isOppfyllerKravForAutomatiskRegistrering()).isFalse();
    }

    @Test
    public void skalIkkeHenteArbeidsforholdDersomBrukerIkkeOppfyllerKravOmAlder()  {
        mockOppfolgingMedRespons(inaktivBrukerMedInaktiveringsDato(LocalDate.now().minusYears(2)));
        getStartRegistreringStatus(FNR_OPPFYLLER_IKKE_KRAV);
        verify(arbeidsforholdService, never()).hentArbeidsforhold(any());
    }

    @Test
    public void skalIkkeHenteArbeidsforholdOmBrukerErUnderOppfolging() {
        mockOppfolgingMedRespons(setOppfolgingsflagg());
        getStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        verify(arbeidsforholdService, never()).hentArbeidsforhold(any());
    }


    @Test
    public void skalReturnereFalseOmIkkeUnderOppfolging() {
        mockOppfolgingMedRespons(inaktivBrukerMedInaktiveringsDato(LocalDate.now()));
        mockArbeidsforhold(arbeidsforholdSomOppfyllerKrav());
        StartRegistreringStatus startRegistreringStatus = getStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        assertThat(startRegistreringStatus.isUnderOppfolging()).isFalse();
    }

    @Test
    public void skalReturnereTrueDersomBrukerOppfyllerKrav() {
        mockOppfolgingMedRespons(inaktivBrukerMedInaktiveringsDato(LocalDate.now().minusYears(2)));
        mockArbeidsforhold(arbeidsforholdSomOppfyllerKrav());
        System.setProperty(MIN_ALDER_AUTOMATISK_REGISTRERING, "30");
        System.setProperty(MAX_ALDER_AUTOMATISK_REGISTRERING, "59");
        StartRegistreringStatus startRegistreringStatus = getStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        assertThat(startRegistreringStatus.isOppfyllerKravForAutomatiskRegistrering()).isTrue();
    }


    private List<Arbeidsforhold> arbeidsforholdSomOppfyllerKrav() {
        return Collections.singletonList(new Arbeidsforhold()
                .setArbeidsgiverOrgnummer("orgnummer")
                .setStyrk("styrk")
                .setFom(LocalDate.of(2017,1,10)));
    }


    private OppfolgingStatus inaktivBrukerMedInaktiveringsDato(LocalDate inaktivFra) {
        return new OppfolgingStatus().setInaktiveringsdato(inaktivFra).setUnderOppfolging(false);
    }

    private void mockOppfolgingMedRespons(OppfolgingStatus oppfolgingStatus){
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(of(oppfolgingStatus));
    }

    private OppfolgingStatus setOppfolgingsflagg(){
        return new OppfolgingStatus().setInaktiveringsdato(null).setUnderOppfolging(true);
    }



    @SneakyThrows
    private StartRegistreringStatus getStartRegistreringStatus(String fnr) {
        return brukerRegistreringService.hentStartRegistreringStatus(fnr);
    }

    @SneakyThrows
    private void mockArbeidsforhold(List<Arbeidsforhold> arbeidsforhold) {
        when(arbeidsforholdService.hentArbeidsforhold(any())).thenReturn(arbeidsforhold);
    }

    public static BrukerRegistrering getBrukerRegistreringSelvgaaende() {
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


    private BrukerRegistrering registrerBruker(BrukerRegistrering bruker, String fnr) {
        return brukerRegistreringService.registrerBruker(bruker, fnr);
    }

    private void mockBrukerUnderOppfolging() {
        when(arbeidssokerregistreringRepository.lagreBruker(any(), any())).thenReturn(getBrukerRegistreringSelvgaaende());

    }

    private void mockArbeidssokerSomHarAktivOppfolging() {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(
                of(new OppfolgingStatus().setInaktiveringsdato(null).setUnderOppfolging(true))
        );
    }


    private void mockInaktivBruker() {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(
                of(new OppfolgingStatus().setInaktiveringsdato(null).setUnderOppfolging(false))
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