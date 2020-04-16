package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
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

public class PdlOppslagGatewayTest {

    private Provider<HttpServletRequest> requestProvider;

    @Before
    public void setUp() {
        requestProvider = mock(Provider.class);
    }

    @Test
    public void skalHenteOppholdTilPerson() {
        PdlOppslagClient service = new PdlOppslagClient("", requestProvider, null) {
            @Override
            String pdlJson(String fnr, PdlRequest request) {
                return okJson();
            }
        };
        Optional<PdlPerson> person = service.hentPerson(AktorId.valueOf("444hhh"));

        Assert.assertEquals(Oppholdstype.MIDLERTIDIG, person.get().getOpphold().get(0).getType());
    }

    @Test
    public void skalHenteOppholdUtenPeriodeTilPerson() {
        PdlOppslagClient service = new PdlOppslagClient("", requestProvider, null) {
            @Override
            String pdlJson(String fnr, PdlRequest request) {
                return okUtenPerioderJson();
            }
        };
        Optional<PdlPerson> person = service.hentPerson(AktorId.valueOf("444hhh"));

        Assert.assertEquals(Oppholdstype.PERMANENT, person.get().getOpphold().get(0).getType());
    }

    @Test(expected = RuntimeException.class)
    public void skalFeileVedError() {
        PdlOppslagClient service = new PdlOppslagClient("", requestProvider, null) {
            @Override
            String pdlJson(String fnr, PdlRequest request) {
                return feilJson();
            }
        };
        service.hentPerson(AktorId.valueOf("111lll"));
    }

    private final String okJson() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(PdlOppslagClient.class.getResource("/pdl/hentPersonOk.json").toURI()));
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final String okUtenPerioderJson() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(PdlOppslagClient.class.getResource("/pdl/hentPersonOkUtenPerioder.json").toURI()));
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final String feilJson() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(PdlOppslagClient.class.getResource("/pdl/hentPersonError.json").toURI()));
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
