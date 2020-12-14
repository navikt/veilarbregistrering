package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayImpl;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingStatusData;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.InfotrygdData;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykemeldingGatewayImpl;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.time.LocalDate.now;
import static no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder.fodselsnummerOnDateMinusYears;
import static no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SykmeldtRegistreringServiceTest {

    private static final Foedselsnummer FNR_OPPFYLLER_KRAV = fodselsnummerOnDateMinusYears(now(), 40);
    private static final Bruker BRUKER_INTERN = Bruker.of(FNR_OPPFYLLER_KRAV, AktorId.of("AKTØRID"));

    private BrukerRegistreringRepository brukerRegistreringRepository;
    private ManuellRegistreringRepository manuellRegistreringRepository;
    private SykmeldtInfoClient sykeforloepMetadataClient;
    private SykmeldtRegistreringService sykmeldtRegistreringService;
    private OppfolgingClient oppfolgingClient;

    @BeforeEach
    public void setup() {
        brukerRegistreringRepository = mock(BrukerRegistreringRepository.class);
        oppfolgingClient = mock(OppfolgingClient.class);
        sykeforloepMetadataClient = mock(SykmeldtInfoClient.class);
        manuellRegistreringRepository = mock(ManuellRegistreringRepository.class);

        OppfolgingGatewayImpl oppfolgingGateway = new OppfolgingGatewayImpl(oppfolgingClient);

        sykmeldtRegistreringService =
                new SykmeldtRegistreringService(
                        new BrukerTilstandService(
                                oppfolgingGateway,
                                new SykemeldingService(new SykemeldingGatewayImpl(sykeforloepMetadataClient))),
                        oppfolgingGateway,
                        brukerRegistreringRepository,
                        manuellRegistreringRepository);
    }

    @Test
    void skalIkkeRegistrereSykmeldteMedTomBesvarelse() {
        mockSykmeldtBrukerOver39uker();
        mockSykmeldtMedArbeidsgiver();
        SykmeldtRegistrering sykmeldtRegistrering = new SykmeldtRegistrering().setBesvarelse(null);
        assertThrows(RuntimeException.class, () -> sykmeldtRegistreringService.registrerSykmeldt(sykmeldtRegistrering, BRUKER_INTERN, null));
    }

    @Test
    void skalIkkeRegistrereSykmeldtSomIkkeOppfyllerKrav() {
        mockSykmeldtMedArbeidsgiver();
        SykmeldtRegistrering sykmeldtRegistrering = gyldigSykmeldtRegistrering();
        assertThrows(RuntimeException.class, () -> sykmeldtRegistreringService.registrerSykmeldt(sykmeldtRegistrering, BRUKER_INTERN, null));
    }

    @Test
    void gitt_at_veileder_ikke_er_angitt_skal_registrering_lagres_uten_navident() {
        mockSykmeldtBrukerOver39uker();
        mockSykmeldtMedArbeidsgiver();
        when(brukerRegistreringRepository.lagreSykmeldtBruker(any(), any())).thenReturn(5L);

        SykmeldtRegistrering sykmeldtRegistrering = gyldigSykmeldtRegistrering();

        long id = sykmeldtRegistreringService.registrerSykmeldt(sykmeldtRegistrering, BRUKER_INTERN, null);

        assertThat(id).isEqualTo(5);
        verifyZeroInteractions(manuellRegistreringRepository);
    }

    @Test
    void gitt_at_veileder_er_angitt_skal_registrering_lagres_uten_navident() {
        mockSykmeldtBrukerOver39uker();
        mockSykmeldtMedArbeidsgiver();
        when(brukerRegistreringRepository.lagreSykmeldtBruker(any(), any())).thenReturn(5L);

        SykmeldtRegistrering sykmeldtRegistrering = gyldigSykmeldtRegistrering();

        long id = sykmeldtRegistreringService.registrerSykmeldt(sykmeldtRegistrering, BRUKER_INTERN, new NavVeileder("Z123456", "Ustekveikja"));

        assertThat(id).isEqualTo(5);
        verify(manuellRegistreringRepository, times(1)).lagreManuellRegistrering(any());
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

}
