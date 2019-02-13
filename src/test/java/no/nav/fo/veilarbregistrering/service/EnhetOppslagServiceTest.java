package no.nav.fo.veilarbregistrering.service;

import no.nav.fo.veilarbregistrering.domain.NavEnhet;
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
    private HentEnheterService hentEnheterService;

    @BeforeEach
    public void setup(){
        hentEnheterService = mock(HentEnheterService.class);
        enhetOppslagService = new EnhetOppslagService(hentEnheterService);

        List<NavEnhet> enheter = Arrays.asList(
                new NavEnhet("1234", "TEST1"),
                new NavEnhet("5678", "TEST2")
        );
        when(hentEnheterService.hentAlleEnheter()).thenReturn(enheter);
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
