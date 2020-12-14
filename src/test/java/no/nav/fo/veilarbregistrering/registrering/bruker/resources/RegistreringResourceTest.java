package no.nav.fo.veilarbregistrering.registrering.bruker.resources;

import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.besvarelse.FremtidigSituasjonSvar;
import no.nav.fo.veilarbregistrering.besvarelse.HelseHinderSvar;
import no.nav.fo.veilarbregistrering.besvarelse.TilbakeIArbeidSvar;
import no.nav.fo.veilarbregistrering.bruker.*;
import no.nav.fo.veilarbregistrering.registrering.bruker.*;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.junit.Before;
import org.junit.Test;

import static no.nav.fo.veilarbregistrering.bruker.FoedselsnummerTestdataBuilder.aremark;
import static no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class RegistreringResourceTest {

    private VeilarbAbacPepClient pepClient;
    private RegistreringResource registreringResource;
    private UserService userService;
    private BrukerRegistreringService brukerRegistreringService;
    private HentRegistreringService hentRegistreringService;
    private StartRegistreringStatusService startRegistreringStatusService;

    @Before
    public void setup() {
        pepClient = mock(VeilarbAbacPepClient.class);
        userService = mock(UserService.class);
        brukerRegistreringService = mock(BrukerRegistreringService.class);
        hentRegistreringService = mock(HentRegistreringService.class);
        SykmeldtRegistreringService sykmeldtRegistreringService = mock(SykmeldtRegistreringService.class);
        startRegistreringStatusService = mock(StartRegistreringStatusService.class);
        UnleashService unleashService = mock(UnleashService.class);
        InaktivBrukerService inaktivBrukerService = mock(InaktivBrukerService.class);

        registreringResource = new RegistreringResource(
                pepClient,
                userService,
                brukerRegistreringService,
                hentRegistreringService,
                unleashService,
                sykmeldtRegistreringService,
                startRegistreringStatusService,
                inaktivBrukerService);
    }

    @Test
    public void skalSjekkeTilgangTilBrukerVedHentingAvStartRegistreringsstatus() {
        when(startRegistreringStatusService.hentStartRegistreringStatus(any())).thenReturn(new StartRegistreringStatusDto());
        when(userService.finnBrukerGjennomPdl()).thenReturn(Bruker.of(aremark(), AktorId.of("1234")));
        registreringResource.hentStartRegistreringStatus();
        verify(pepClient, times(1)).sjekkLesetilgangTilBruker(any());
    }

    @Test
    public void skalFeileVedHentingAvStartRegistreringsstatusMedUgyldigFnr() {
        when(startRegistreringStatusService.hentStartRegistreringStatus(any())).thenReturn(new StartRegistreringStatusDto());
        when(userService.finnBrukerGjennomPdl()).thenCallRealMethod();
        when(userService.getFnrFromUrl()).thenReturn("ugyldigFnr");
        assertThrows(RuntimeException.class, () -> registreringResource.hentRegistrering());
        verify(pepClient, times(0)).sjekkLesetilgangTilBruker(any());
    }

    @Test
    public void skalSjekkeTilgangTilBrukerVedHentingAvRegistrering() {
        when(hentRegistreringService.hentOrdinaerBrukerRegistrering(any(Bruker.class))).thenReturn(gyldigBrukerRegistrering());
        when(hentRegistreringService.hentSykmeldtRegistrering(any(Bruker.class))).thenReturn(null);
        when(userService.finnBrukerGjennomPdl()).thenReturn(Bruker.of(aremark(), AktorId.of("1234")));
        registreringResource.hentRegistrering();
        verify(pepClient, times(1)).sjekkLesetilgangTilBruker(any());
    }

    @Test
    public void skalSjekkeTilgangTilBrukerVedRegistreringSykmeldt() {
        SykmeldtRegistrering sykmeldtRegistrering = new SykmeldtRegistrering()
                .setBesvarelse(new Besvarelse()
                        .setFremtidigSituasjon(FremtidigSituasjonSvar.SAMME_ARBEIDSGIVER)
                        .setTilbakeIArbeid(TilbakeIArbeidSvar.JA_FULL_STILLING));
        when(userService.finnBrukerGjennomPdl()).thenReturn(Bruker.of(aremark(), AktorId.of("1234")));
        registreringResource.registrerSykmeldt(sykmeldtRegistrering);
        verify(pepClient, times(1)).sjekkSkrivetilgangTilBruker(any());
    }

    @Test
    public void skalSjekkeTilgangTilBrukerVedRegistreringAvBruker() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = new OrdinaerBrukerRegistrering()
                .setBesvarelse(new Besvarelse().setHelseHinder(HelseHinderSvar.NEI)).setId(2L);

        when(userService.finnBrukerGjennomPdl()).thenReturn(Bruker.of(aremark(), AktorId.of("1234")));
        when(brukerRegistreringService.registrerBrukerUtenOverforing(ordinaerBrukerRegistrering, Bruker.of(aremark(), AktorId.of("1234")), null)).thenReturn(ordinaerBrukerRegistrering);
        registreringResource.registrerBruker(ordinaerBrukerRegistrering);

        verify(pepClient, times(1)).sjekkSkrivetilgangTilBruker(any());
    }
}