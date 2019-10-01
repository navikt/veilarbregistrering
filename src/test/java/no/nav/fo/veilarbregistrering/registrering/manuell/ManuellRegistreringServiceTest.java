package no.nav.fo.veilarbregistrering.registrering.manuell;

import no.nav.fo.veilarbregistrering.orgenhet.HentEnheterGateway;
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet;
import no.nav.fo.veilarbregistrering.orgenhet.adapter.HentEnheterGatewayImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ManuellRegistreringServiceTest {

    private ManuellRegistreringRepository manuellRegistreringRepository;
    private HentEnheterGateway hentEnheterGateway;
    private ManuellRegistreringService manuellRegistreringService;

    @BeforeEach
    public void setup(){
        manuellRegistreringRepository = mock(ManuellRegistreringRepository.class);
        hentEnheterGateway = mock(HentEnheterGatewayImpl.class);
        manuellRegistreringService = new ManuellRegistreringService(manuellRegistreringRepository, hentEnheterGateway);

        List<NavEnhet> enheter = Arrays.asList(
                new NavEnhet("1234", "TEST1"),
                new NavEnhet("5678", "TEST2")
        );
        when(hentEnheterGateway.hentAlleEnheter()).thenReturn(enheter);
    }
    @Test
    public void skalFinneRiktigEnhet(){
        Optional<NavEnhet> enhet = manuellRegistreringService.finnEnhet("1234");
        assertThat(enhet).hasValue(new NavEnhet("1234", "TEST1"));
    }

    @Test
    public void skalReturnereEmptyHvisIngenEnhetErFunnet(){
        Optional<NavEnhet> enhet = manuellRegistreringService.finnEnhet("2345");
        assertThat(enhet).isEmpty();
    }

}