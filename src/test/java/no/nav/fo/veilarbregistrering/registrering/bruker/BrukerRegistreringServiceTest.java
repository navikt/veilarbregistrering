package no.nav.fo.veilarbregistrering.registrering.bruker;

import lombok.SneakyThrows;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.adapter.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.config.RemoteFeatureConfig;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.InfotrygdData;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingStatusData;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.SykmeldtBrukerType;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.Optional.of;
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.SYKMELDT_REGISTRERING;
import static no.nav.fo.veilarbregistrering.utils.TestUtils.getFodselsnummerForPersonWithAge;
import static no.nav.fo.veilarbregistrering.utils.TestUtils.gyldigBrukerRegistrering;
import static no.nav.fo.veilarbregistrering.utils.TestUtils.gyldigSykmeldtRegistrering;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BrukerRegistreringServiceTest {

    private static String FNR_OPPFYLLER_KRAV = getFodselsnummerForPersonWithAge(40);

    private ArbeidssokerregistreringRepository arbeidssokerregistreringRepository;
    private SykmeldtInfoClient sykeforloepMetadataClient;
    private AktorService aktorService;
    private BrukerRegistreringService brukerRegistreringService;
    private OppfolgingClient oppfolgingClient;
    private ArbeidsforholdGateway arbeidsforholdGateway;
    private StartRegistreringUtils startRegistreringUtils;
    private ManuellRegistreringService manuellRegistreringService;
    private RemoteFeatureConfig.SykemeldtRegistreringFeature sykemeldtRegistreringFeature;

    @BeforeEach
    public void setup() {
        sykemeldtRegistreringFeature = mock(RemoteFeatureConfig.SykemeldtRegistreringFeature.class);
        aktorService = mock(AktorService.class);
        arbeidssokerregistreringRepository = mock(ArbeidssokerregistreringRepository.class);
        manuellRegistreringService = mock(ManuellRegistreringService.class);
        oppfolgingClient = mock(OppfolgingClient.class);
        sykeforloepMetadataClient = mock(SykmeldtInfoClient.class);
        arbeidsforholdGateway = mock(ArbeidsforholdGateway.class);
        startRegistreringUtils = new StartRegistreringUtils();

        brukerRegistreringService =
                new BrukerRegistreringService(
                        arbeidssokerregistreringRepository,
                        aktorService,
                        oppfolgingClient,
                        sykeforloepMetadataClient,
                        arbeidsforholdGateway,
                        manuellRegistreringService,
                        startRegistreringUtils,
                        sykemeldtRegistreringFeature);

        when(aktorService.getAktorId(any())).thenReturn(of("AKTORID"));
        when(sykemeldtRegistreringFeature.erSykemeldtRegistreringAktiv()).thenReturn(true);
    }

    /*
     * Test av besvarelsene og lagring
     * */
    @Test
    void skalRegistrereSelvgaaendeBruker() {
        mockInaktivBrukerUtenReaktivering();
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring();
        OrdinaerBrukerRegistrering selvgaaendeBruker = gyldigBrukerRegistrering();
        when(arbeidssokerregistreringRepository.lagreOrdinaerBruker(any(OrdinaerBrukerRegistrering.class), any(AktorId.class))).thenReturn(selvgaaendeBruker);
        registrerBruker(selvgaaendeBruker, FNR_OPPFYLLER_KRAV);
        verify(arbeidssokerregistreringRepository, times(1)).lagreOrdinaerBruker(any(), any());
    }

    @Test
    void skalReaktivereInaktivBrukerUnder28Dager() {
        mockInaktivBrukerSomSkalReaktiveres();
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring();

        brukerRegistreringService.reaktiverBruker(FNR_OPPFYLLER_KRAV);
        verify(arbeidssokerregistreringRepository, times(1)).lagreReaktiveringForBruker(any());
    }

    @Test
    void reaktiveringAvBrukerOver28DagerSkalGiException() {
        mockInaktivBrukerSomSkalReaktiveres();
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring();
        mockOppfolgingMedRespons(
                new OppfolgingStatusData()
                        .withUnderOppfolging(false)
                        .withKanReaktiveres(false)
        );
        assertThrows(RuntimeException.class, () -> brukerRegistreringService.reaktiverBruker(FNR_OPPFYLLER_KRAV));
        verify(arbeidssokerregistreringRepository, times(0)).lagreReaktiveringForBruker(any());
    }

    @Test
    void skalRegistrereSelvgaaendeBrukerIDatabasen() {
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring();
        mockOppfolgingMedRespons(new OppfolgingStatusData().withUnderOppfolging(false).withKanReaktiveres(false));
        OrdinaerBrukerRegistrering selvgaaendeBruker = gyldigBrukerRegistrering();
        when(arbeidssokerregistreringRepository.lagreOrdinaerBruker(any(OrdinaerBrukerRegistrering.class), any(AktorId.class))).thenReturn(selvgaaendeBruker);
        registrerBruker(selvgaaendeBruker, FNR_OPPFYLLER_KRAV);
        verify(oppfolgingClient, times(1)).aktiverBruker(any());
        verify(arbeidssokerregistreringRepository, times(1)).lagreOrdinaerBruker(any(), any());
    }

    @Test
    void skalIkkeLagreRegistreringSomErUnderOppfolging() {
        mockBrukerUnderOppfolging();
        OrdinaerBrukerRegistrering selvgaaendeBruker = gyldigBrukerRegistrering();
        assertThrows(RuntimeException.class, () -> registrerBruker(selvgaaendeBruker, FNR_OPPFYLLER_KRAV));
    }

    @Test
    public void skalReturnereUnderOppfolgingNaarUnderOppfolging() {
        mockArbeidssokerSomHarAktivOppfolging();
        StartRegistreringStatus startRegistreringStatus = brukerRegistreringService.hentStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        assertThat(startRegistreringStatus.getRegistreringType() == RegistreringType.ALLEREDE_REGISTRERT).isTrue();
    }

    @Test
    public void skalReturnereAtBrukerOppfyllerBetingelseOmArbeidserfaring() {
        mockInaktivBrukerUtenReaktivering();
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring();
        StartRegistreringStatus startRegistreringStatus = brukerRegistreringService.hentStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        assertThat(startRegistreringStatus.getJobbetSeksAvTolvSisteManeder()).isTrue();
    }

    @Test
    public void skalReturnereFalseOmIkkeUnderOppfolging() {
        mockOppfolgingMedRespons(inaktivBruker());
        mockArbeidsforhold(arbeidsforholdSomOppfyllerKrav());
        StartRegistreringStatus startRegistreringStatus = getStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        assertThat(startRegistreringStatus.getRegistreringType() == RegistreringType.ALLEREDE_REGISTRERT).isFalse();
    }

    @Test
    void skalRegistrereSykmeldte() {
        mockSykmeldtMedArbeidsgiver();
        mockSykmeldtBrukerOver39uker();
        SykmeldtRegistrering sykmeldtRegistrering = gyldigSykmeldtRegistrering();
        brukerRegistreringService.registrerSykmeldt(sykmeldtRegistrering, FNR_OPPFYLLER_KRAV);

        SykmeldtBrukerType sykmeldtBrukerType = startRegistreringUtils.finnSykmeldtBrukerType(sykmeldtRegistrering);

        verify(oppfolgingClient, times(1)).settOppfolgingSykmeldt(sykmeldtBrukerType, FNR_OPPFYLLER_KRAV);
    }

    @Test
    void skalIkkeRegistrereSykmeldteMedTomBesvarelse() {
        mockSykmeldtBrukerOver39uker();
        mockSykmeldtMedArbeidsgiver();
        SykmeldtRegistrering sykmeldtRegistrering = new SykmeldtRegistrering().setBesvarelse(null);
        assertThrows(RuntimeException.class, () -> brukerRegistreringService.registrerSykmeldt(sykmeldtRegistrering, FNR_OPPFYLLER_KRAV));
    }

    @Test
    void skalIkkeRegistrereSykmeldtSomIkkeOppfyllerKrav() {
        mockSykmeldtMedArbeidsgiver();
        SykmeldtRegistrering sykmeldtRegistrering = gyldigSykmeldtRegistrering();
        assertThrows(RuntimeException.class, () -> brukerRegistreringService.registrerSykmeldt(sykmeldtRegistrering, FNR_OPPFYLLER_KRAV));
    }

    @Test
    public void skalReturnereAlleredeUnderOppfolging() {
        mockArbeidssokerSomHarAktivOppfolging();
        StartRegistreringStatus startRegistreringStatus = getStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        assertThat(startRegistreringStatus.getRegistreringType() == RegistreringType.ALLEREDE_REGISTRERT).isTrue();
    }

    @Test
    public void skalReturnereReaktivering() {
        mockOppfolgingMedRespons(inaktivBruker());
        mockArbeidsforhold(arbeidsforholdSomOppfyllerKrav());
        StartRegistreringStatus startRegistreringStatus = getStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        assertThat(startRegistreringStatus.getRegistreringType() == RegistreringType.REAKTIVERING).isTrue();
    }

    @Test
    public void skalReturnereSykmeldtRegistrering() {
        mockSykmeldtBruker();
        mockSykmeldtBrukerOver39uker();
        StartRegistreringStatus startRegistreringStatus = getStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        assertThat(startRegistreringStatus.getRegistreringType() == SYKMELDT_REGISTRERING).isTrue();
    }

    @Test
    public void skalReturnereSperret() {
        mockSykmeldtBruker();
        mockSykmeldtBrukerUnder39uker();
        StartRegistreringStatus startRegistreringStatus = getStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        assertThat(startRegistreringStatus.getRegistreringType() == RegistreringType.SPERRET).isTrue();
    }

    @Test
    public void skalReturnereOrdinarRegistrering() {
        mockIkkeSykmeldtBruker();
        StartRegistreringStatus startRegistreringStatus = getStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        assertThat(startRegistreringStatus.getRegistreringType() == RegistreringType.ORDINAER_REGISTRERING).isTrue();
    }

    @Test
    public void mockDataSkalIkkeGjeldeNaarMockToggleErAv() {
        mockSykmeldtBruker();
        mockSykmeldtBrukerUnder39uker();
        StartRegistreringStatus startRegistreringStatus = getStartRegistreringStatus(FNR_OPPFYLLER_KRAV);
        verify(sykeforloepMetadataClient, times(1)).hentSykmeldtInfoData(anyString());
        assertThat(SYKMELDT_REGISTRERING.equals(startRegistreringStatus.getRegistreringType())).isFalse();
    }


    private List<Arbeidsforhold> arbeidsforholdSomOppfyllerKrav() {
        return Collections.singletonList(new Arbeidsforhold()
                .setArbeidsgiverOrgnummer("orgnummer")
                .setStyrk("styrk")
                .setFom(LocalDate.of(2017, 1, 10)));
    }


    private OppfolgingStatusData inaktivBruker() {
        return new OppfolgingStatusData().withUnderOppfolging(false).withKanReaktiveres(true);
    }

    private void mockOppfolgingMedRespons(OppfolgingStatusData oppfolgingStatusData) {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(oppfolgingStatusData);
    }

    @SneakyThrows
    private StartRegistreringStatus getStartRegistreringStatus(String fnr) {
        return brukerRegistreringService.hentStartRegistreringStatus(fnr);
    }

    @SneakyThrows
    private void mockArbeidsforhold(List<Arbeidsforhold> arbeidsforhold) {
        when(arbeidsforholdGateway.hentArbeidsforhold(any())).thenReturn(arbeidsforhold);
    }

    private OrdinaerBrukerRegistrering registrerBruker(OrdinaerBrukerRegistrering bruker, String fnr) {
        return brukerRegistreringService.registrerBruker(bruker, fnr);
    }

    private void mockBrukerUnderOppfolging() {
        when(arbeidssokerregistreringRepository.lagreOrdinaerBruker(any(), any())).thenReturn(gyldigBrukerRegistrering());

    }

    private void mockArbeidssokerSomHarAktivOppfolging() {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(
                new OppfolgingStatusData().withUnderOppfolging(true).withKanReaktiveres(false)
        );
    }

    private void mockInaktivBrukerUtenReaktivering() {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(
                new OppfolgingStatusData().withUnderOppfolging(false).withKanReaktiveres(false)
        );
    }

    private void mockInaktivBrukerSomSkalReaktiveres() {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(
                new OppfolgingStatusData().withUnderOppfolging(false).withKanReaktiveres(true)
        );
    }

    private void mockSykmeldtBruker() {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(
                new OppfolgingStatusData()
                        .withUnderOppfolging(false)
                        .withKanReaktiveres(false)
                        .withErSykmeldtMedArbeidsgiver(true)
        );
    }

    private void mockIkkeSykmeldtBruker() {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(
                new OppfolgingStatusData()
                        .withUnderOppfolging(false)
                        .withKanReaktiveres(false)
                        .withErSykmeldtMedArbeidsgiver(false)
        );
    }

    private void mockSykmeldtBrukerOver39uker() {
        String dagensDatoMinus13Uker = now().plusWeeks(13).toString();
        when(sykeforloepMetadataClient.hentSykmeldtInfoData(anyString())).thenReturn(
                new InfotrygdData()
                        .withMaksDato(dagensDatoMinus13Uker)
        );
    }

    private void mockSykmeldtBrukerUnder39uker() {
        String dagensDatoMinus14Uker = now().plusWeeks(14).toString();
        when(sykeforloepMetadataClient.hentSykmeldtInfoData(anyString())).thenReturn(
                new InfotrygdData()
                        .withMaksDato(dagensDatoMinus14Uker)
        );
    }


    private void mockSykmeldtMedArbeidsgiver() {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(
                new OppfolgingStatusData().withErSykmeldtMedArbeidsgiver(true).withKanReaktiveres(false)
        );
    }

    private void mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring() {
        when(arbeidsforholdGateway.hentArbeidsforhold(any())).thenReturn(
                Collections.singletonList(new Arbeidsforhold()
                        .setArbeidsgiverOrgnummer("orgnummer")
                        .setStyrk("styrk")
                        .setFom(LocalDate.of(2017, 1, 10)))
        );
    }
}
