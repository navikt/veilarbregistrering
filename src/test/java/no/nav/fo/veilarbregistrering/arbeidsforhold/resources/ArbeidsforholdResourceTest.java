package no.nav.fo.veilarbregistrering.arbeidsforhold.resources;

import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ArbeidsforholdResourceTest {

    private VeilarbAbacPepClient pepClient;
    private ArbeidsforholdResource arbeidsforholdResource;
    private UserService userService;
    private ArbeidsforholdGateway arbeidsforholdGateway;
    private AktorService aktorService;

    private static String IDENT = "10108000398"; //Aremark fiktivt fnr.";

    @BeforeEach
    public void setup() {
        pepClient = mock(VeilarbAbacPepClient.class);
        userService = mock(UserService.class);
        arbeidsforholdGateway = mock(ArbeidsforholdGateway.class);
        aktorService = mock(AktorService.class);

        arbeidsforholdResource = new ArbeidsforholdResource(
                pepClient,
                userService,
                arbeidsforholdGateway,
                aktorService
        );
    }

    @Test
    public void skalSjekkeTilgangTilBrukerVedHentingAvSisteArbeidsforhold() {
        when(userService.hentFnrFraUrlEllerToken()).thenReturn(IDENT);
        arbeidsforholdResource.hentSisteArbeidsforhold();
        verify(pepClient, times(1)).sjekkLesetilgangTilBruker(any());
    }

}
