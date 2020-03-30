package no.nav.fo.veilarbregistrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.pdl.*;
import no.nav.sbl.featuretoggle.unleash.UnleashService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PdlServiceTest {

    private Provider<HttpServletRequest> requestProvider;
    private UnleashService unleash;

    @Before
    public void setUp() {
        unleash = mock(UnleashService.class);
        requestProvider = mock(Provider.class);
    }

    @Test
    public void skalHenteOppholdTilPerson() {
        PdlOppslagService service = new PdlOppslagService("", requestProvider, unleash) {
            @Override
            String pdlJson(String fnr, PdlRequest request) {
                return okJson();
            }
        };
        when(unleash.isEnabled(any())).thenReturn(true);
        Optional<PdlPerson> person = service.hentPerson("");

        Assert.assertEquals(Oppholdstype.MIDLERTIDIG, person.get().getOpphold().get(0).getType());
    }

    @Test(expected = RuntimeException.class)
    public void skalFeileVedError() {
        PdlOppslagService service = new PdlOppslagService("", requestProvider, unleash) {
            @Override
            String pdlJson(String fnr, PdlRequest request) {
                return feilJson();
            }
        };
        when(unleash.isEnabled(any())).thenReturn(true);
        service.hentPerson("");
    }

    private final String okJson() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(PdlOppslagService.class.getResource("/pdl/hentPersonOk.json").toURI()));
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final String feilJson() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(PdlOppslagService.class.getResource("/pdl/hentPersonError.json").toURI()));
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
