package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.Mockito.mock;

public class PdlOppslagClientTest {

    private Provider<HttpServletRequest> requestProvider;

    @Before
    public void setUp() {
        requestProvider = mock(Provider.class);
    }

    @Test
    public void skalHenteOppholdTilPerson() {
        PdlOppslagClient client = new PdlOppslagClient("", requestProvider, null) {
            @Override
            String pdlJson(String fnr, PdlRequest request) {
                return okJson();
            }
        };
        PdlPerson person = client.hentPerson(AktorId.valueOf("444hhh"));

        Assert.assertEquals(Oppholdstype.MIDLERTIDIG, person.getOpphold().get(0).getType());
    }

    private String okJson() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(PdlOppslagClient.class.getResource("/pdl/hentPersonOk.json").toURI()));
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void skalHenteOppholdUtenPeriodeTilPerson() {
        PdlOppslagClient service = new PdlOppslagClient("", requestProvider, null) {
            @Override
            String pdlJson(String fnr, PdlRequest request) {
                return okUtenPerioderJson();
            }
        };
        PdlPerson person = service.hentPerson(AktorId.valueOf("444hhh"));

        Assert.assertEquals(Oppholdstype.PERMANENT, person.getOpphold().get(0).getType());
    }

    private String okUtenPerioderJson() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(PdlOppslagClient.class.getResource("/pdl/hentPersonOkUtenPerioder.json").toURI()));
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test(expected = RuntimeException.class)
    public void skalFeileVedError() {
        PdlOppslagClient pdlOppslagClient = new PdlOppslagClient("", requestProvider, null) {
            @Override
            String pdlJson(String fnr, PdlRequest request) {
                return feilJson();
            }
        };
        pdlOppslagClient.hentPerson(AktorId.valueOf("111lll"));
    }

    private String feilJson() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(PdlOppslagClient.class.getResource("/pdl/hentPersonError.json").toURI()));
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test(expected = RuntimeException.class)
    public void skalFeileVedNotFound() {
        PdlOppslagClient pdlOppslagClient = new PdlOppslagClient("", requestProvider, null) {
            @Override
            String pdlJson(String fnr, PdlRequest request) {
                return personNotFound();
            }
        };
        pdlOppslagClient.hentPerson(AktorId.valueOf("111lll"));
    }

    private String personNotFound() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(PdlOppslagClient.class.getResource("/pdl/hentPersonNotFound.json").toURI()));
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
