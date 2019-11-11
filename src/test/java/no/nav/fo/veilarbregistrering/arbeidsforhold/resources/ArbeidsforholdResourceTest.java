package no.nav.fo.veilarbregistrering.arbeidsforhold.resources;

import no.nav.apiapp.security.veilarbabac.VeilarbAbacPepClient;
import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.arbeidsforhold.Arbeidsforhold;
import no.nav.fo.veilarbregistrering.arbeidsforhold.ArbeidsforholdGateway;
import no.nav.fo.veilarbregistrering.arbeidsforhold.FlereArbeidsforhold;
import no.nav.fo.veilarbregistrering.bruker.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static java.util.Arrays.asList;
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
        when(arbeidsforholdGateway.hentFlereArbeidsforhold(IDENT)).thenReturn(flereArbeidsforhold());
        arbeidsforholdResource.hentSisteArbeidsforhold();
        verify(pepClient, times(1)).sjekkLesetilgangTilBruker(any());
    }

    private FlereArbeidsforhold flereArbeidsforhold() {
        LocalDate fom3 = LocalDate.of(2017,1,1);
        LocalDate tom3 = LocalDate.of(2017,11,30);
        LocalDate fom2 = LocalDate.of(2017,10,1);
        LocalDate tom2 = LocalDate.of(2017,11,30);
        LocalDate fom1 = LocalDate.of(2017,11,1);
        LocalDate tom1 = LocalDate.of(2017,11,30);

        Arbeidsforhold sisteArbeidsforholdVarighet3 = new Arbeidsforhold().setFom(fom3).setTom(tom3);
        Arbeidsforhold sisteArbeidsforholdvarighet2 = new Arbeidsforhold().setFom(fom2).setTom(tom2);
        Arbeidsforhold sisteArbeidsforholdVarighet1 = new Arbeidsforhold().setFom(fom1).setTom(tom1);

        return FlereArbeidsforhold.of(asList(
                sisteArbeidsforholdVarighet1,
                sisteArbeidsforholdvarighet2,
                sisteArbeidsforholdVarighet3));
    }

}
