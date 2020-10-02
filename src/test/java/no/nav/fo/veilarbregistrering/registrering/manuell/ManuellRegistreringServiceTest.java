package no.nav.fo.veilarbregistrering.registrering.manuell;

import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr;
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ManuellRegistreringServiceTest {

    private ManuellRegistreringService manuellRegistreringService;

    @BeforeEach
    public void setup(){
        ManuellRegistreringRepository manuellRegistreringRepository = mock(ManuellRegistreringRepository.class);
        UnleashService unleashService = mock(UnleashService.class);
        when(unleashService.isEnabled(any())).thenReturn(true);
        Norg2Gateway norg2Gateway = mock(Norg2Gateway.class);
        manuellRegistreringService = new ManuellRegistreringService(manuellRegistreringRepository, norg2Gateway);

        Map<Enhetnr, NavEnhet> enheter = new HashMap();
        enheter.put(Enhetnr.of("1234"), new NavEnhet(Enhetnr.of("1234"), "TEST1"));
        enheter.put(Enhetnr.of("5678"), new NavEnhet(Enhetnr.of("5678"), "TEST2"));

        when(norg2Gateway.hentAlleEnheter()).thenReturn(enheter);
    }
    @Test
    public void skalFinneRiktigEnhet(){
        Optional<NavEnhet> enhet = manuellRegistreringService.finnEnhetViaRest(Enhetnr.of("1234"));
        assertThat(enhet).hasValue(new NavEnhet(Enhetnr.of("1234"), "TEST1"));
    }

    @Test
    public void skalReturnereEmptyHvisIngenEnhetErFunnet(){
        Optional<NavEnhet> enhet = manuellRegistreringService.finnEnhetViaRest(Enhetnr.of("2345"));
        assertThat(enhet).isEmpty();
    }
}