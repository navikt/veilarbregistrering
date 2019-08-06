package no.nav.fo.veilarbregistrering.service;

import no.nav.dialogarena.aktor.AktorService;
import no.nav.fo.veilarbregistrering.db.ArbeidssokerregistreringRepository;
import no.nav.fo.veilarbregistrering.orgenhet.EnhetOppslagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManuellRegistreringServiceTest {

    private final static String MOCK_AKTOR_ID = "7986543233647548";


    private AktorService aktorService;
    private ArbeidssokerregistreringRepository arbeidssokerregistreringRepository;
    private Provider<HttpServletRequest> requestProvider;
    private EnhetOppslagService enhetOppslagService;
    private ManuellRegistreringService manuellRegistreringService;

    @BeforeEach
    public void setup() {

        aktorService = mock(AktorService.class);
        arbeidssokerregistreringRepository = mock(ArbeidssokerregistreringRepository.class);
        requestProvider = mock(Provider.class);
        enhetOppslagService = mock(EnhetOppslagService.class);

        manuellRegistreringService = new ManuellRegistreringService(aktorService,
                arbeidssokerregistreringRepository, enhetOppslagService, requestProvider);

        when(aktorService.getAktorId(any())).thenReturn(of(MOCK_AKTOR_ID));
    }

    @Test
    public void skalHenteEnhetIdFraUrl(){
        String enhetId = "1234";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("enhetId")).thenReturn(enhetId);
        when(requestProvider.get()).thenReturn(request);

        String enhetIdFraUrl = manuellRegistreringService.getEnhetIdFromUrlOrThrow();
        assertEquals(enhetId, enhetIdFraUrl);
    }

    @Test
    public void skalFeileHvisUrlIkkeHarEnhetId(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("enhetId")).thenReturn(null);
        when(requestProvider.get()).thenReturn(request);

        assertThrows(RuntimeException.class, () -> manuellRegistreringService.getEnhetIdFromUrlOrThrow());
    }

}
