package no.nav.fo.veilarbregistrering.service;

import lombok.SneakyThrows;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.domain.*;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.domain.besvarelse.HelseHinderSvar;
import no.nav.fo.veilarbregistrering.domain.besvarelse.Stilling;
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
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring();
        BrukerRegistrering selvgaaendeBruker = getBrukerRegistreringSelvgaaende();
        when(arbeidssokerregistreringRepository.lagreBruker(any(BrukerRegistrering.class), any(AktorId.class))).thenReturn(selvgaaendeBruker);
        registrerBruker(selvgaaendeBruker, FNR_OPPFYLLER_KRAV);
        verify(arbeidssokerregistreringRepository, times(1)).lagreBruker(any(), any());
    }

    @Test
    void skalRegistrereSelvgaaendeBrukerIDatabasen()  {
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring();
        mockOppfolgingMedRespons(new AktivStatus().withUnderOppfolging(false));
        BrukerRegistrering selvgaaendeBruker = getBrukerRegistreringSelvgaaende();
        when(arbeidssokerregistreringRepository.lagreBruker(any(BrukerRegistrering.class), any(AktorId.class))).thenReturn(selvgaaendeBruker);
        registrerBruker(selvgaaendeBruker, FNR_OPPFYLLER_KRAV);
        verify(oppfolgingClient, times(1)).aktiverBruker(any());
        verify(arbeidssokerregistreringRepository, times(1)).lagreBruker(any(), any());
    }

    @Test
    void skalKasteRuntimeExceptionDersomRegistreringFeatureErAv()  {
        when(registreringFeature.erAktiv()).thenReturn(false);
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring();
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
    public void skalReturnereUnderOppfolgingNaarUnderOppfolging() {
        mockArbeidssokerSomHarAktivOppfolging();
        StartRegistreringStatus startRegistreringStatus = brukerRegistreringService.hentStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        assertThat(startRegistreringStatus.isUnderOppfolging()).isTrue();
    }

    @Test
    public void skalReturnereAtBrukerOppfyllerBetingelseOmArbeidserfaring() {
        mockInaktivBruker();
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring();
        StartRegistreringStatus startRegistreringStatus = brukerRegistreringService.hentStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        assertThat(startRegistreringStatus.isJobbetSeksAvTolvSisteManeder()).isTrue();
    }

    @Test
    public void skalReturnereAtBrukerOppfyllerBetingelseOmInaktivitet() {
        mockOppfolgingsstatusSomOppfyllerBetingelseOmInaktivitet();
        StartRegistreringStatus startRegistreringStatus = brukerRegistreringService.hentStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        assertThat(startRegistreringStatus.isRegistrertNavSisteToAr()).isFalse();
    }

    @Test
    public void skalReturnereFalseOmIkkeUnderOppfolging() {
        mockOppfolgingMedRespons(inaktivBrukerMedInaktiveringsDato(LocalDate.now()));
        mockArbeidsforhold(arbeidsforholdSomOppfyllerKrav());
        StartRegistreringStatus startRegistreringStatus = getStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        assertThat(startRegistreringStatus.isUnderOppfolging()).isFalse();
    }

    private List<Arbeidsforhold> arbeidsforholdSomOppfyllerKrav() {
        return Collections.singletonList(new Arbeidsforhold()
                .setArbeidsgiverOrgnummer("orgnummer")
                .setStyrk("styrk")
                .setFom(LocalDate.of(2017,1,10)));
    }


    private AktivStatus inaktivBrukerMedInaktiveringsDato(LocalDate inaktivFra) {
        return new AktivStatus().withInaktiveringDato(inaktivFra).withUnderOppfolging(false).withAktiv(false);
    }

    private void mockOppfolgingMedRespons(AktivStatus aktivStatus){
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(aktivStatus);
    }

    private AktivStatus setOppfolgingsflagg(){
        return new AktivStatus().withInaktiveringDato(null).withUnderOppfolging(true).withAktiv(true);
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
        return new BrukerRegistrering()
                .setNusKode(NUS_KODE_4)
                .setSisteStilling(new Stilling().setStyrk08("1111.11"))
                .setOpprettetDato(null)
                .setEnigIOppsummering(ENIG_I_OPPSUMMERING)
                .setOppsummering(OPPSUMMERING)
                .setBesvarelse(new Besvarelse().setHelseHinder(HelseHinderSvar.NEI))

                // TODO: Skal slettes. FO-1123
                .setHarHelseutfordringer(false)
                .setYrkesPraksis("1111.11");
    }

    private BrukerRegistrering getBrukerIngenUtdannelse() {
        return new BrukerRegistrering()
                .setNusKode(NUS_KODE_0)
                .setSisteStilling(new Stilling().setStyrk08(null))
                .setOpprettetDato(null)
                .setEnigIOppsummering(ENIG_I_OPPSUMMERING)
                .setOppsummering(OPPSUMMERING)
                .setBesvarelse(new Besvarelse().setHelseHinder(HelseHinderSvar.NEI));
    }

    private BrukerRegistrering getBrukerRegistreringMedHelseutfordringer() {
        return new BrukerRegistrering()
                .setNusKode(NUS_KODE_4)
                .setSisteStilling(new Stilling().setStyrk08(null))
                .setOpprettetDato(null)
                .setEnigIOppsummering(ENIG_I_OPPSUMMERING)
                .setOppsummering(OPPSUMMERING)
                .setBesvarelse(new Besvarelse().setHelseHinder(HelseHinderSvar.JA));
    }


    private BrukerRegistrering registrerBruker(BrukerRegistrering bruker, String fnr) {
        return brukerRegistreringService.registrerBruker(bruker, fnr);
    }

    private void mockBrukerUnderOppfolging() {
        when(arbeidssokerregistreringRepository.lagreBruker(any(), any())).thenReturn(getBrukerRegistreringSelvgaaende());

    }

    private void mockArbeidssokerSomHarAktivOppfolging() {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(
                new AktivStatus().withInaktiveringDato(null).withUnderOppfolging(true).withAktiv(true)
        );
    }

    private void mockInaktivBruker() {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(
                new AktivStatus().withInaktiveringDato(null).withUnderOppfolging(false).withAktiv(false)
        );
    }

    private void mockInaktivBrukerMedOppfolgingsflagg() {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(
                new AktivStatus().withInaktiveringDato(null).withUnderOppfolging(true).withAktiv(false)
        );
    }

    private void mockOppfolgingsstatusSomOppfyllerBetingelseOmInaktivitet() {
        mockOppfolgingMedRespons(inaktivBrukerMedInaktiveringsDato(LocalDate.now().minusYears(2)));
    }

    private void mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring() {
        when(arbeidsforholdService.hentArbeidsforhold(any())).thenReturn(
                Collections.singletonList(new Arbeidsforhold()
                        .setArbeidsgiverOrgnummer("orgnummer")
                        .setStyrk("styrk")
                        .setFom(LocalDate.of(2017,1,10)))
        );
    }
}