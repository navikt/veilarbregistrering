package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.FnrUtilsTest;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayImpl;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingStatusData;
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository;
import no.nav.fo.veilarbregistrering.profilering.ProfileringService;
import no.nav.fo.veilarbregistrering.registrering.resources.RegistreringTilstandDto;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.InfotrygdData;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykemeldingGatewayImpl;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static java.time.LocalDate.now;
import static no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class BrukerRegistreringServiceTest {

    private static final Foedselsnummer FNR_OPPFYLLER_KRAV = Foedselsnummer.of(FnrUtilsTest.getFodselsnummerOnDateMinusYears(now(), 40));
    private static final Bruker BRUKER_INTERN = Bruker.of(FNR_OPPFYLLER_KRAV, AktorId.of("AKTØRID"));

    private BrukerRegistreringRepository brukerRegistreringRepository;
    private AktiveringTilstandRepository aktiveringTilstandRepository;
    private SykmeldtInfoClient sykeforloepMetadataClient;
    private BrukerRegistreringService brukerRegistreringService;
    private OppfolgingClient oppfolgingClient;
    private ArbeidsforholdGateway arbeidsforholdGateway;

    @BeforeEach
    public void setup() {
        brukerRegistreringRepository = mock(BrukerRegistreringRepository.class);
        ProfileringRepository profileringRepository = mock(ProfileringRepository.class);
        oppfolgingClient = mock(OppfolgingClient.class);
        sykeforloepMetadataClient = mock(SykmeldtInfoClient.class);
        arbeidsforholdGateway = mock(ArbeidsforholdGateway.class);
        ProfileringService profileringService = new ProfileringService(arbeidsforholdGateway);
        ArbeidssokerRegistrertProducer arbeidssokerRegistrertProducer = (event) -> {
        }; //NoOp siden vi ikke ønsker å teste Kafka her
        ArbeidssokerProfilertProducer arbeidssokerProfilertProducer = (aktorId, innsatsgruppe, profilertDato) -> {
        };
        aktiveringTilstandRepository = mock(AktiveringTilstandRepository.class);

        OppfolgingGatewayImpl oppfolgingGateway = new OppfolgingGatewayImpl(oppfolgingClient);

        brukerRegistreringService =
                new BrukerRegistreringService(
                        brukerRegistreringRepository,
                        profileringRepository,
                        oppfolgingGateway,
                        profileringService,
                        arbeidssokerRegistrertProducer,
                        arbeidssokerProfilertProducer,
                        aktiveringTilstandRepository,
                        new BrukerTilstandService(
                                oppfolgingGateway,
                                new SykemeldingService(new SykemeldingGatewayImpl(sykeforloepMetadataClient))));
    }

    /*
     * Test av besvarelsene og lagring
     * */
    @Test
    void skalRegistrereSelvgaaendeBruker() {
        mockInaktivBrukerUtenReaktivering();
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring();
        OrdinaerBrukerRegistrering selvgaaendeBruker = gyldigBrukerRegistrering();
        when(brukerRegistreringRepository.lagre(any(OrdinaerBrukerRegistrering.class), any(Bruker.class))).thenReturn(selvgaaendeBruker);
        registrerBruker(selvgaaendeBruker, BRUKER_INTERN);
        verify(brukerRegistreringRepository, times(1)).lagre(any(), any());
    }

    @Test
    void skalReaktivereInaktivBrukerUnder28Dager() {
        mockInaktivBrukerSomSkalReaktiveres();
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring();

        brukerRegistreringService.reaktiverBruker(BRUKER_INTERN);
        verify(brukerRegistreringRepository, times(1)).lagreReaktiveringForBruker(any());
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
        assertThrows(RuntimeException.class, () -> brukerRegistreringService.reaktiverBruker(BRUKER_INTERN));
        verify(brukerRegistreringRepository, times(0)).lagreReaktiveringForBruker(any());
    }

    @Test
    void skalRegistrereSelvgaaendeBrukerIDatabasen() {
        mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring();
        mockOppfolgingMedRespons(new OppfolgingStatusData().withUnderOppfolging(false).withKanReaktiveres(false));
        OrdinaerBrukerRegistrering selvgaaendeBruker = gyldigBrukerRegistrering();
        when(brukerRegistreringRepository.lagre(any(OrdinaerBrukerRegistrering.class), any(Bruker.class))).thenReturn(selvgaaendeBruker);
        registrerBruker(selvgaaendeBruker, BRUKER_INTERN);
        verify(oppfolgingClient, times(1)).aktiverBruker(any());
        verify(brukerRegistreringRepository, times(1)).lagre(any(), any());
    }

    @Test
    void skalIkkeLagreRegistreringSomErUnderOppfolging() {
        mockBrukerUnderOppfolging();
        OrdinaerBrukerRegistrering selvgaaendeBruker = gyldigBrukerRegistrering();
        assertThrows(RuntimeException.class, () -> registrerBruker(selvgaaendeBruker, BRUKER_INTERN));
    }

    @Test
    void skalIkkeRegistrereSykmeldteMedTomBesvarelse() {
        mockSykmeldtBrukerOver39uker();
        mockSykmeldtMedArbeidsgiver();
        SykmeldtRegistrering sykmeldtRegistrering = new SykmeldtRegistrering().setBesvarelse(null);
        assertThrows(RuntimeException.class, () -> brukerRegistreringService.registrerSykmeldt(sykmeldtRegistrering, BRUKER_INTERN));
    }

    @Test
    void skalIkkeRegistrereSykmeldtSomIkkeOppfyllerKrav() {
        mockSykmeldtMedArbeidsgiver();
        SykmeldtRegistrering sykmeldtRegistrering = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering();
        assertThrows(RuntimeException.class, () -> brukerRegistreringService.registrerSykmeldt(sykmeldtRegistrering, BRUKER_INTERN));
    }

    @Test
    public void skal_oppdatere_registreringtilstand_med_status_og_sistendret() {
        LocalDateTime sistEndret = LocalDateTime.now();
        AktiveringTilstand original = RegistreringTilstandTestdataBuilder
                .registreringTilstand()
                .status(Status.ARENA_OK)
                .opprettet(sistEndret.minusDays(1))
                .sistEndret(sistEndret)
                .build();
        when(aktiveringTilstandRepository.hentAktiveringTilstand(original.getId())).thenReturn(original);

        brukerRegistreringService.oppdaterRegistreringTilstand(RegistreringTilstandDto.of(original.getId(), Status.MANGLER_ARBEIDSTILLATELSE));

        ArgumentCaptor<AktiveringTilstand> argumentCaptor = ArgumentCaptor.forClass(AktiveringTilstand.class);
        verify(aktiveringTilstandRepository).oppdater(argumentCaptor.capture());
        AktiveringTilstand capturedArgument = argumentCaptor.getValue();

        assertThat(capturedArgument.getId()).isEqualTo(original.getId());
        assertThat(capturedArgument.getUuid()).isEqualTo(original.getUuid());
        assertThat(capturedArgument.getBrukerRegistreringId()).isEqualTo(original.getBrukerRegistreringId());
        assertThat(capturedArgument.getOpprettet()).isEqualTo(original.getOpprettet());
        assertThat(capturedArgument.getStatus()).isEqualTo(Status.MANGLER_ARBEIDSTILLATELSE);

    }

    private void mockOppfolgingMedRespons(OppfolgingStatusData oppfolgingStatusData) {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(oppfolgingStatusData);
    }

    private void registrerBruker(OrdinaerBrukerRegistrering ordinaerBrukerRegistrering, Bruker bruker) {
        brukerRegistreringService.registrerBruker(ordinaerBrukerRegistrering, bruker);
    }

    private void mockBrukerUnderOppfolging() {
        when(brukerRegistreringRepository.lagre(any(), any())).thenReturn(gyldigBrukerRegistrering());

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


    private void mockSykmeldtBrukerOver39uker() {
        String dagensDatoMinus13Uker = now().plusWeeks(13).toString();
        when(sykeforloepMetadataClient.hentSykmeldtInfoData(any())).thenReturn(
                new InfotrygdData()
                        .withMaksDato(dagensDatoMinus13Uker)
        );
    }

    private void mockSykmeldtMedArbeidsgiver() {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(
                new OppfolgingStatusData().withErSykmeldtMedArbeidsgiver(true).withKanReaktiveres(false)
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
}
