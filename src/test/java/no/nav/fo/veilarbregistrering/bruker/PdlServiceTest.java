package no.nav.fo.veilarbregistrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.pdl.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PdlServiceTest {

    private PdlOppslagService pdlOppslagService;
    private Provider<HttpServletRequest> requestProvider;

    @Before
    public void setUp() {
        requestProvider = mock(Provider.class);
        pdlOppslagService = new PdlOppslagService();
    }

    @Test
    @Disabled
    public void skalHenteOppholdTilPerson() {

        PdlOppslagService service = mock(PdlOppslagService.class);
        when(service.pdlJson(any(), any())).thenReturn(okJson());

        PdlPerson person = service.hentPerson("");

        Assert.assertEquals(Oppholdstype.MIDLERTIDIG, person.getOpphold().get(0).getType());
    }

    @Disabled
    @Test(expected = RuntimeException.class)
    public void skalFeileHvisBrukerIkkeFinnes() {

    }

    private final String okJson() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(PdlOppslagService.class.getResource("/pdl/hentPersonOk.json").toURI()));
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
