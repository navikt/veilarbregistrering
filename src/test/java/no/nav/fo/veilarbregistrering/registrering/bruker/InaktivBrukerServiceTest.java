package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayImpl;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingStatusData;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykemeldingGatewayImpl;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.time.LocalDate.now;
import static no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder.fodselsnummerOnDateMinusYears;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InaktivBrukerServiceTest {

    private static final Foedselsnummer FNR_OPPFYLLER_KRAV = fodselsnummerOnDateMinusYears(now(), 40);
    private static final Bruker BRUKER_INTERN = Bruker.of(FNR_OPPFYLLER_KRAV, AktorId.of("AKTØRID"));

    private InaktivBrukerService inaktivBrukerService;
    
    private BrukerRegistreringRepository brukerRegistreringRepository;
    private SykmeldtInfoClient sykeforloepMetadataClient;
    private OppfolgingClient oppfolgingClient;

    @BeforeEach
    public void setup() {
        brukerRegistreringRepository = mock(BrukerRegistreringRepository.class);
        oppfolgingClient = mock(OppfolgingClient.class);
        when(oppfolgingClient.reaktiverBruker(any())).thenReturn(AktiverBrukerResultat.Companion.ok());
        sykeforloepMetadataClient = mock(SykmeldtInfoClient.class);

        OppfolgingGatewayImpl oppfolgingGateway = new OppfolgingGatewayImpl(oppfolgingClient);

        inaktivBrukerService =
                new InaktivBrukerService(
                        new BrukerTilstandService(
                                oppfolgingGateway,
                                new SykemeldingService(new SykemeldingGatewayImpl(sykeforloepMetadataClient))),
                        brukerRegistreringRepository,
                        oppfolgingGateway);
    }


    @Test
    void skalReaktivereInaktivBrukerUnder28Dager() {
        mockInaktivBrukerSomSkalReaktiveres();

        inaktivBrukerService.reaktiverBruker(BRUKER_INTERN);
        verify(brukerRegistreringRepository, times(1)).lagreReaktiveringForBruker(any());
    }

    @Test
    void reaktiveringAvBrukerOver28DagerSkalGiException() {
        mockInaktivBrukerSomSkalReaktiveres();
        mockOppfolgingMedRespons(
                new OppfolgingStatusData()
                        .withUnderOppfolging(false)
                        .withKanReaktiveres(false)
        );
        assertThrows(RuntimeException.class, () -> inaktivBrukerService.reaktiverBruker(BRUKER_INTERN));
        verify(brukerRegistreringRepository, times(0)).lagreReaktiveringForBruker(any());
    }

    private void mockInaktivBrukerSomSkalReaktiveres() {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(
                new OppfolgingStatusData().withUnderOppfolging(false).withKanReaktiveres(true)
        );
    }

    private void mockOppfolgingMedRespons(OppfolgingStatusData oppfolgingStatusData) {
        when(oppfolgingClient.hentOppfolgingsstatus(any())).thenReturn(oppfolgingStatusData);
    }
}
