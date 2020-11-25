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
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykemeldingGatewayImpl;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;

import static java.time.LocalDate.now;
import static no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering;
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
        aktiveringTilstandRepository = mock(AktiveringTilstandRepository.class);

        OppfolgingGatewayImpl oppfolgingGateway = new OppfolgingGatewayImpl(oppfolgingClient);

        brukerRegistreringService =
                new BrukerRegistreringService(
                        brukerRegistreringRepository,
                        profileringRepository,
                        oppfolgingGateway,
                        profileringService,
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

    private void mockArbeidssforholdSomOppfyllerBetingelseOmArbeidserfaring() {
        when(arbeidsforholdGateway.hentArbeidsforhold(any())).thenReturn(
                FlereArbeidsforhold.of(Collections.singletonList(new Arbeidsforhold()
                        .setArbeidsgiverOrgnummer("orgnummer")
                        .setStyrk("styrk")
                        .setFom(LocalDate.of(2017, 1, 10))))
        );
    }
}
