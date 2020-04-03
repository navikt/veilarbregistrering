package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.FnrUtilsTest;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingGatewayImpl;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingStatusData;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.InfotrygdData;
import no.nav.fo.veilarbregistrering.sykemelding.adapter.SykmeldtInfoClient;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SykmeldtBrukerRegistreringServiceTest {

    private static Foedselsnummer FNR_OPPFYLLER_KRAV = Foedselsnummer.of(FnrUtilsTest.getFodselsnummerOnDateMinusYears(now(), 40));
    private static Bruker BRUKER_INTERN = Bruker.of(FNR_OPPFYLLER_KRAV, AktorId.valueOf("AKTØRID"));

    private BrukerRegistreringService brukerRegistreringService;
    private BrukerRegistreringRepository brukerRegistreringRepository;
    private SykmeldtInfoClient sykeforloepMetadataClient;
    private SykmeldtBrukerRegistreringService sykmeldtBrukerRegistreringService;
    private OppfolgingClient oppfolgingClient;
    private UnleashService unleashService;

    @BeforeEach
    public void setup() {
        brukerRegistreringService = mock(BrukerRegistreringService.class);
        brukerRegistreringRepository = mock(BrukerRegistreringRepository.class);
        unleashService = mock(UnleashService.class);
        oppfolgingClient = mock(OppfolgingClient.class);
        sykeforloepMetadataClient = mock(SykmeldtInfoClient.class);

        sykmeldtBrukerRegistreringService =
                new SykmeldtBrukerRegistreringService(
                        brukerRegistreringService,
                        brukerRegistreringRepository,
                        new OppfolgingGatewayImpl(oppfolgingClient),
                        unleashService);

        when(unleashService.isEnabled("veilarbregistrering.lagreTilstandErAktiv")).thenReturn(true);
        when(unleashService.isEnabled("veilarbregistrering.lagreUtenArenaOverforing")).thenReturn(false);
    }

    @Test
    void skalIkkeRegistrereSykmeldteMedTomBesvarelse() {
        mockSykmeldtBrukerOver39uker();
        mockSykmeldtMedArbeidsgiver();
        SykmeldtRegistrering sykmeldtRegistrering = new SykmeldtRegistrering().setBesvarelse(null);
        assertThrows(RuntimeException.class, () -> sykmeldtBrukerRegistreringService.registrerSykmeldt(sykmeldtRegistrering, BRUKER_INTERN));
    }

    @Test
    void skalIkkeRegistrereSykmeldtSomIkkeOppfyllerKrav() {
        mockSykmeldtMedArbeidsgiver();
        SykmeldtRegistrering sykmeldtRegistrering = SykmeldtRegistreringTestdataBuilder.gyldigSykmeldtRegistrering();
        assertThrows(RuntimeException.class, () -> sykmeldtBrukerRegistreringService.registrerSykmeldt(sykmeldtRegistrering, BRUKER_INTERN));
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
