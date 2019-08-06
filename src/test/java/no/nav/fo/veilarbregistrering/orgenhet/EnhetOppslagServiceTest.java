package no.nav.fo.veilarbregistrering.orgenhet;

import no.nav.fo.veilarbregistrering.orgenhet.adapter.HentEnheterGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnhetOppslagServiceTest {

    private EnhetOppslagService enhetOppslagService;
    private HentEnheterGateway hentEnheterGateway;

    @BeforeEach
    public void setup(){
        hentEnheterGateway = mock(HentEnheterGateway.class);
        enhetOppslagService = new EnhetOppslagService(hentEnheterGateway);

        List<NavEnhet> enheter = Arrays.asList(
                new NavEnhet("1234", "TEST1"),
                new NavEnhet("5678", "TEST2")
        );
        when(hentEnheterGateway.hentAlleEnheter()).thenReturn(enheter);
    }

    @Test
    public void skalFinneRiktigEnhet(){
         NavEnhet enhet = enhetOppslagService.finnEnhet("1234");
         assertEquals(enhet, new NavEnhet("1234", "TEST1"));
    }

    @Test
    public void skalReturnereNullHvisIngenEnhetErFunnet(){
        NavEnhet enhet = enhetOppslagService.finnEnhet("2345");
        assertNull(enhet);
    }

}
