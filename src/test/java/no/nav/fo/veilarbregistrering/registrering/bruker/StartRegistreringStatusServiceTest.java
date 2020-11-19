package no.nav.fo.veilarbregistrering.registrering.bruker;

import lombok.SneakyThrows;
import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayImpl;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingStatusData;
import no.nav.fo.veilarbregistrering.registrering.resources.StartRegistreringStatusDto;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.InfotrygdData;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykemeldingGatewayImpl;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDate.now;
import static no.nav.fo.veilarbregistrering.registrering.bruker.RegistreringType.SYKMELDT_REGISTRERING;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StartRegistreringStatusServiceTest {

    private static final Foedselsnummer FNR_OPPFYLLER_KRAV = Foedselsnummer.of(FnrUtilsTest.getFodselsnummerOnDateMinusYears(now(), 40));
    private static final Bruker BRUKER_INTERN = Bruker.of(FNR_OPPFYLLER_KRAV, AktorId.of("AKTØRID"));

    private StartRegistreringStatusService brukerRegistreringService;

    private ArbeidsforholdGateway arbeidsforholdGateway;
    private SykmeldtInfoClient sykeforloepMetadataClient;
    private OppfolgingClient oppfolgingClient;
    private PersonGateway personGateway;

    @BeforeEach
    public void setup() {
        arbeidsforholdGateway = mock(ArbeidsforholdGateway.class);
        oppfolgingClient = mock(OppfolgingClient.class);
        OppfolgingGatewayImpl oppfolgingGateway = new OppfolgingGatewayImpl(oppfolgingClient);
        sykeforloepMetadataClient = mock(SykmeldtInfoClient.class);
        SykemeldingService sykemeldingService = new SykemeldingService(new SykemeldingGatewayImpl(sykeforloepMetadataClient));
        personGateway = mock(PersonGateway.class);

        brukerRegistreringService = new StartRegistreringStatusService(
                    arbeidsforholdGateway,
                    new BrukerTilstandService(oppfolgingGateway, sykemeldingService),
                    personGateway);
    }

    @Test
    public void skalReturnereUnderOppfolgingNaarUnderOppfolging() {
        mockArbeidssokerSomHarAktivOppfolging();
        StartRegistreringStatusDto startRegistreringStatus = brukerRegistreringService.hentStartRegistreringStatus(BRUKER_INTERN);
        assertThat(startRegistreringStatus.getRegistreringType() == RegistreringType.ALLEREDE_REGISTRERT).isTrue();
    }

    @Test
    public void skalReturnereAtBrukerOppfyllerBetingelseOmArbeidserfaring() {
        mockInaktivBrukerUtenReaktivering();
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring();
        StartRegistreringStatusDto startRegistreringStatus = brukerRegistreringService.hentStartRegistreringStatus(BRUKER_INTERN);
        assertThat(startRegistreringStatus.getJobbetSeksAvTolvSisteManeder()).isTrue();
    }

    @Test
    public void skalReturnereFalseOmIkkeUnderOppfolging() {
        mockOppfolgingMedRespons(inaktivBruker());
        mockArbeidsforhold(arbeidsforholdSomOppfyllerKrav());
        StartRegistreringStatusDto startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN);
        assertThat(startRegistreringStatus.getRegistreringType() == RegistreringType.ALLEREDE_REGISTRERT).isFalse();
    }

    @Test
    public void skalReturnereAlleredeUnderOppfolging() {
        mockArbeidssokerSomHarAktivOppfolging();
        StartRegistreringStatusDto startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN);
        assertThat(startRegistreringStatus.getRegistreringType() == RegistreringType.ALLEREDE_REGISTRERT).isTrue();
    }

    @Test
    public void skalReturnereReaktivering() {
        mockOppfolgingMedRespons(inaktivBruker());
        mockArbeidsforhold(arbeidsforholdSomOppfyllerKrav());
        StartRegistreringStatusDto startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN);
        assertThat(startRegistreringStatus.getRegistreringType()).isEqualTo(RegistreringType.REAKTIVERING);
    }

    @Test
    public void skalReturnereSykmeldtRegistrering() {
        mockSykmeldtBruker();
        mockSykmeldtBrukerOver39uker();
        StartRegistreringStatusDto startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN);
        assertThat(startRegistreringStatus.getRegistreringType()).isEqualTo(SYKMELDT_REGISTRERING);
    }

    @Test
    public void skalReturnereSperret() {
        mockSykmeldtBruker();
        mockSykmeldtBrukerUnder39uker();
        StartRegistreringStatusDto startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN);
        assertThat(startRegistreringStatus.getRegistreringType()).isEqualTo(RegistreringType.SPERRET);
    }

    @Test
    public void gitt_at_geografiskTilknytning_ikke_ble_funnet_skal_null_returneres() {
        mockInaktivBrukerUtenReaktivering();
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring();
        StartRegistreringStatusDto startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN);
        assertThat(startRegistreringStatus).isNotNull();
        assertThat(startRegistreringStatus.getGeografiskTilknytning()).isNull();

    }

    @Test
    public void gitt_at_geografiskTilknytning_er_1234_skal_1234_returneres() {
        mockInaktivBrukerUtenReaktivering();
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring();
        when(personGateway.hentGeografiskTilknytning(any())).thenReturn(Optional.of(GeografiskTilknytning.of("1234")));
        StartRegistreringStatusDto startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN);
        assertThat(startRegistreringStatus).isNotNull();
        assertThat(startRegistreringStatus.getGeografiskTilknytning()).isEqualTo("1234");
    }

    @Test
    public void gitt_at_geografiskTilknytning_kaster_exception_skal_null_returneres() {
        mockInaktivBrukerUtenReaktivering();
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring();
        when(personGateway.hentGeografiskTilknytning(any())).thenThrow(new RuntimeException("Ikke tilgang"));
        StartRegistreringStatusDto startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN);
        assertThat(startRegistreringStatus).isNotNull();
        assertThat(startRegistreringStatus.getGeografiskTilknytning()).isNull();
    }

    @Test
    public void skalReturnereOrdinarRegistrering() {
        mockIkkeSykmeldtBruker();
        mockArbeidsforhold(arbeidsforholdSomOppfyllerKrav());
        StartRegistreringStatusDto startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN);
        assertThat(startRegistreringStatus.getRegistreringType() == RegistreringType.ORDINAER_REGISTRERING).isTrue();
    }

    @Test
    public void mockDataSkalIkkeGjeldeNaarMockToggleErAv() {
        mockSykmeldtBruker();
        mockSykmeldtBrukerUnder39uker();
        StartRegistreringStatusDto startRegistreringStatus = getStartRegistreringStatus(BRUKER_INTERN);
        verify(sykeforloepMetadataClient, times(1)).hentSykmeldtInfoData(any());
        assertThat(SYKMELDT_REGISTRERING.equals(startRegistreringStatus.getRegistreringType())).isFalse();
    }

    @SneakyThrows
    private StartRegistreringStatusDto getStartRegistreringStatus(Bruker bruker) {
        return brukerRegistreringService.hentStartRegistreringStatus(bruker);
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

    private void mockArbeidssokerSomHarAktivOppfolging() {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(
                new OppfolgingStatusData().withUnderOppfolging(true).withKanReaktiveres(false)
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

    private void mockSykmeldtBrukerUnder39uker() {
        String dagensDatoMinus14Uker = now().plusWeeks(14).toString();
        when(sykeforloepMetadataClient.hentSykmeldtInfoData(any())).thenReturn(
                new InfotrygdData()
                        .withMaksDato(dagensDatoMinus14Uker)
        );
    }

    @SneakyThrows
    private void mockArbeidsforhold(List<Arbeidsforhold> arbeidsforhold) {
        when(arbeidsforholdGateway.hentArbeidsforhold(any())).thenReturn(FlereArbeidsforhold.of(arbeidsforhold));
    }

    private void mockInaktivBrukerUtenReaktivering() {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(
                new OppfolgingStatusData().withUnderOppfolging(false).withKanReaktiveres(false)
        );
    }

    private void mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring() {
        when(arbeidsforholdGateway.hentArbeidsforhold(any())).thenReturn(
                FlereArbeidsforhold.of(Collections.singletonList(new Arbeidsforhold()
                        .setArbeidsgiverOrgnummer("orgnummer")
                        .setStyrk("styrk")
                        .setFom(LocalDate.of(2017, 1, 10))))
        );
    }

    private void mockOppfolgingMedRespons(OppfolgingStatusData oppfolgingStatusData) {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(oppfolgingStatusData);
    }

    private void mockSykmeldtBrukerOver39uker() {
        String dagensDatoMinus13Uker = now().plusWeeks(13).toString();
        when(sykeforloepMetadataClient.hentSykmeldtInfoData(any())).thenReturn(
                new InfotrygdData()
                        .withMaksDato(dagensDatoMinus13Uker)
        );
    }
}