package no.nav.fo.veilarbregistrering.bruker;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserService userService;
    private Provider<HttpServletRequest> requestProvider;

    @Before
    public void setUp() {
        requestProvider = mock(Provider.class);
        userService = new UserService(requestProvider, null);
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
}
