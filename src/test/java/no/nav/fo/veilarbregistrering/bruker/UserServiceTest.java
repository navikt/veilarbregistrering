package no.nav.fo.veilarbregistrering.bruker;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserService userService;
    private Provider<HttpServletRequest> requestProvider;
    private PdlOppslagGateway pdlOppslagGateway;

    @Before
    public void setUp() {
        requestProvider = mock(Provider.class);
        pdlOppslagGateway = mock(PdlOppslagGateway.class);
        userService = new UserService(requestProvider, null, pdlOppslagGateway);
    }

    @Test
    public void skalHenteEnhetIdFraUrl(){
        String enhetId = "1234";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("enhetId")).thenReturn(enhetId);
        when(requestProvider.get()).thenReturn(request);

        String enhetIdFraUrl = userService.getEnhetIdFromUrlOrThrow();
        Assert.assertEquals(enhetId, enhetIdFraUrl);
    }

    @Test(expected = RuntimeException.class)
    public void skalFeileHvisUrlIkkeHarEnhetId(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("enhetId")).thenReturn(null);
        when(requestProvider.get()).thenReturn(request);

        userService.getEnhetIdFromUrlOrThrow();
    }

    @Test()
    public void skalFinneBrukerGjennomPdl() {
        when(pdlOppslagGateway.hentIdenter(Foedselsnummer.of("11111111111"))).thenReturn(new Identer(Arrays.asList(
                new Ident("11111111111", false, Gruppe.FOLKEREGISTERIDENT),
                new Ident("22222222222", false, Gruppe.AKTORID),
                new Ident("33333333333", false, Gruppe.NPID)
        )));

        Bruker bruker = userService.finnBrukerGjennomPdl(Foedselsnummer.of("11111111111"));

        assertThat(bruker.getGjeldendeFoedselsnummer().stringValue()).isEqualTo("11111111111");
        assertThat(bruker.getAktorId().asString()).isEqualTo("22222222222");
    }
}
