package no.nav.fo.veilarbregistrering.registrering.resources;

import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.fo.veilarbregistrering.besvarelse.Besvarelse;
import no.nav.fo.veilarbregistrering.besvarelse.FremtidigSituasjonSvar;
import no.nav.fo.veilarbregistrering.besvarelse.HelseHinderSvar;
import no.nav.fo.veilarbregistrering.besvarelse.TilbakeIArbeidSvar;
import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringService;
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringWrapper;
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistrering;
import no.nav.fo.veilarbregistrering.registrering.bruker.SykmeldtRegistrering;
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringService;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.junit.Before;
import org.junit.Test;

import static no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class RegistreringResourceTest {

    private VeilarbAbacPepClient pepClient;
    private RegistreringResource registreringResource;
    private UserService userService;
    private BrukerRegistreringService brukerRegistreringService;

    private static String IDENT = "10108000398"; //Aremark fiktivt fnr.";

    @Before
    public void setup() {
        pepClient = mock(VeilarbAbacPepClient.class);
        userService = mock(UserService.class);
        ManuellRegistreringService manuellRegistreringService = mock(ManuellRegistreringService.class);
        brukerRegistreringService = mock(BrukerRegistreringService.class);
        UnleashService unleashService = mock(UnleashService.class);

        registreringResource = new RegistreringResource(
                pepClient,
                userService,
                manuellRegistreringService,
                brukerRegistreringService,
                unleashService
        );
    }

    @Test
    public void skalSjekkeTilgangTilBrukerVedHentingAvStartRegistreringsstatus() {
        when(brukerRegistreringService.hentStartRegistreringStatus(any())).thenReturn(new StartRegistreringStatusDto());
        when(userService.hentBruker()).thenReturn(Bruker.of(Foedselsnummer.of(IDENT), AktorId.of("1234")));
        registreringResource.hentStartRegistreringStatus();
        verify(pepClient, times(1)).sjekkLesetilgangTilBruker(any());
    }

    @Test
    public void skalFeileVedHentingAvStartRegistreringsstatusMedUgyldigFnr() {
        when(brukerRegistreringService.hentStartRegistreringStatus(any())).thenReturn(new StartRegistreringStatusDto());
        when(userService.hentBruker()).thenCallRealMethod();
        when(userService.getFnrFromUrl()).thenReturn("ugyldigFnr");
        assertThrows(RuntimeException.class, () -> registreringResource.hentRegistrering());
        verify(pepClient, times(0)).sjekkLesetilgangTilBruker(any());
    }

    @Test
    public void skalSjekkeTilgangTilBrukerVedHentingAvRegistrering() {
        BrukerRegistreringWrapper response = new BrukerRegistreringWrapper(gyldigBrukerRegistrering());
        when(brukerRegistreringService.hentBrukerRegistrering(any(Bruker.class))).thenReturn(response);
        when(userService.hentBruker()).thenReturn(Bruker.of(Foedselsnummer.of(IDENT), AktorId.of("1234")));
        registreringResource.hentRegistrering();
        verify(pepClient, times(1)).sjekkLesetilgangTilBruker(any());
    }

    @Test
    public void skalSjekkeTilgangTilBrukerVedRegistreringSykmeldt() {
        SykmeldtRegistrering sykmeldtRegistrering = new SykmeldtRegistrering()
                .setBesvarelse(new Besvarelse()
                        .setFremtidigSituasjon(FremtidigSituasjonSvar.SAMME_ARBEIDSGIVER)
                        .setTilbakeIArbeid(TilbakeIArbeidSvar.JA_FULL_STILLING));
        when(userService.hentBruker()).thenReturn(Bruker.of(Foedselsnummer.of(IDENT), AktorId.of("1234")));
        registreringResource.registrerSykmeldt(sykmeldtRegistrering);
        verify(pepClient, times(1)).sjekkSkrivetilgangTilBruker(any());
    }

    @Test
    public void skalSjekkeTilgangTilBrukerVedRegistreringAvBruker() {
        OrdinaerBrukerRegistrering ordinaerBrukerRegistrering = new OrdinaerBrukerRegistrering()
                .setBesvarelse(new Besvarelse().setHelseHinder(HelseHinderSvar.NEI));

        when(userService.hentBruker()).thenReturn(Bruker.of(Foedselsnummer.of(IDENT), AktorId.of("1234")));
        registreringResource.registrerBruker(ordinaerBrukerRegistrering);
        verify(pepClient, times(1)).sjekkSkrivetilgangTilBruker(any());
    }

}
