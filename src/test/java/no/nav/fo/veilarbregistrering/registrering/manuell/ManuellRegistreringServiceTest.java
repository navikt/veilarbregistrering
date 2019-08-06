package no.nav.fo.veilarbregistrering.registrering.manuell;

import no.nav.fo.veilarbregistrering.orgenhet.EnhetOppslagService;
import no.nav.fo.veilarbregistrering.registrering.manuell.db.ManuellRegistreringRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

public class ManuellRegistreringServiceTest {

    private final static String MOCK_AKTOR_ID = "7986543233647548";

    private ManuellRegistreringRepositoryImpl manuellRegistreringRepository;
    private Provider<HttpServletRequest> requestProvider;
    private EnhetOppslagService enhetOppslagService;
    private ManuellRegistreringService manuellRegistreringService;

    @BeforeEach
    public void setup() {

        manuellRegistreringRepository = Mockito.mock(ManuellRegistreringRepositoryImpl.class);
        requestProvider = Mockito.mock(Provider.class);
        enhetOppslagService = Mockito.mock(EnhetOppslagService.class);

        manuellRegistreringService = new ManuellRegistreringService(
                manuellRegistreringRepository,
                enhetOppslagService,
                requestProvider);
    }

    @Test
    public void skalHenteEnhetIdFraUrl(){
        String enhetId = "1234";
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getParameter("enhetId")).thenReturn(enhetId);
        Mockito.when(requestProvider.get()).thenReturn(request);

        String enhetIdFraUrl = manuellRegistreringService.getEnhetIdFromUrlOrThrow();
        Assertions.assertEquals(enhetId, enhetIdFraUrl);
    }

    @Test
    public void skalFeileHvisUrlIkkeHarEnhetId(){
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getParameter("enhetId")).thenReturn(null);
        Mockito.when(requestProvider.get()).thenReturn(request);

        Assertions.assertThrows(RuntimeException.class, () -> manuellRegistreringService.getEnhetIdFromUrlOrThrow());
    }

}
