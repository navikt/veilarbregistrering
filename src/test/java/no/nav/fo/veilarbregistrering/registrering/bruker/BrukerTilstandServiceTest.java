package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.common.featuretoggle.UnleashService;
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe;
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway;
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus;
import no.nav.fo.veilarbregistrering.oppfolging.Rettighetsgruppe;
import no.nav.fo.veilarbregistrering.oppfolging.Servicegruppe;
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService;
import no.nav.fo.veilarbregistrering.sykemelding.SykmeldtInfoData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BrukerTilstandServiceTest {

    private OppfolgingGateway oppfolgingGateway;
    private SykemeldingService sykemeldingService;
    private UnleashService unleashService;

    private BrukerTilstandService brukerTilstandService;

    @BeforeEach
    public void setUp() {
        oppfolgingGateway = mock(OppfolgingGateway.class);
        sykemeldingService = mock(SykemeldingService.class);
        unleashService = mock(UnleashService.class);

        brukerTilstandService = new BrukerTilstandService(oppfolgingGateway, sykemeldingService, unleashService);
    }

    @Test
    public void brukersTilstand_skal_gi_sperret_når_toggle_ikke_er_skrudd_på() {
        Oppfolgingsstatus oppfolgingsstatus = new Oppfolgingsstatus(
                false,
                false,
                true,
                Formidlingsgruppe.of("IARBS"),
                Servicegruppe.of("VURDI"),
                Rettighetsgruppe.of("IYT"));
        when(oppfolgingGateway.hentOppfolgingsstatus(any())).thenReturn(oppfolgingsstatus);

        SykmeldtInfoData sykeforlop = new SykmeldtInfoData(null, false);
        when(sykemeldingService.hentSykmeldtInfoData(any())).thenReturn(sykeforlop);

        when(unleashService.isEnabled(any())).thenReturn(false);

        BrukersTilstand brukersTilstand = brukerTilstandService.hentBrukersTilstand(any());

        assertThat(brukersTilstand.getRegistreringstype()).isEqualTo(RegistreringType.SPERRET);
    }

    @Test
    public void brukersTilstand_skal_gi_sykmeldtRegistrering_når_toggle_er_enablet() {
        Oppfolgingsstatus oppfolgingsstatus = new Oppfolgingsstatus(
                false,
                false,
                true,
                Formidlingsgruppe.of("IARBS"),
                Servicegruppe.of("VURDI"),
                Rettighetsgruppe.of("IYT"));
        when(oppfolgingGateway.hentOppfolgingsstatus(any())).thenReturn(oppfolgingsstatus);

        SykmeldtInfoData sykeforlop = new SykmeldtInfoData(null, false);
        when(sykemeldingService.hentSykmeldtInfoData(any())).thenReturn(sykeforlop);

        when(unleashService.isEnabled(any())).thenReturn(true);

        BrukersTilstand brukersTilstand = brukerTilstandService.hentBrukersTilstand(any());

        assertThat(brukersTilstand.getRegistreringstype()).isEqualTo(RegistreringType.SYKMELDT_REGISTRERING);
    }

    @Test
    public void brukersTilstand_hvor_med_og_uten_maksdato_gir_ulike_svar() {
        Oppfolgingsstatus oppfolgingsstatus = new Oppfolgingsstatus(
                false,
                false,
                true,
                Formidlingsgruppe.of("IARBS"),
                Servicegruppe.of("VURDI"),
                Rettighetsgruppe.of("IYT"));
        when(oppfolgingGateway.hentOppfolgingsstatus(any())).thenReturn(oppfolgingsstatus);

        SykmeldtInfoData sykeforlop = new SykmeldtInfoData(null, false);
        when(sykemeldingService.hentSykmeldtInfoData(any())).thenReturn(sykeforlop);

        when(unleashService.isEnabled(any())).thenReturn(true);

        BrukersTilstand brukersTilstand = brukerTilstandService.hentBrukersTilstand(any(), true);

        assertThat(brukersTilstand.getRegistreringstype()).isEqualTo(RegistreringType.SYKMELDT_REGISTRERING);
    }

    @Test
    public void brukersTilstand_hvor_med_og_uten_maksdato_gir_like_svar() {
        Oppfolgingsstatus oppfolgingsstatus = new Oppfolgingsstatus(
                false,
                false,
                true,
                Formidlingsgruppe.of("IARBS"),
                Servicegruppe.of("VURDI"),
                Rettighetsgruppe.of("IYT"));
        when(oppfolgingGateway.hentOppfolgingsstatus(any())).thenReturn(oppfolgingsstatus);

        SykmeldtInfoData sykeforlop = new SykmeldtInfoData(LocalDate.now().minusWeeks(10).toString(), true);
        when(sykemeldingService.hentSykmeldtInfoData(any())).thenReturn(sykeforlop);

        when(unleashService.isEnabled(any())).thenReturn(true);

        BrukersTilstand brukersTilstand = brukerTilstandService.hentBrukersTilstand(any(), true);

        assertThat(brukersTilstand.getRegistreringstype()).isEqualTo(RegistreringType.SYKMELDT_REGISTRERING);
    }
}
