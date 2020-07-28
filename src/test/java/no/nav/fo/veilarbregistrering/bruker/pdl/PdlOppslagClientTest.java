package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.BrukerIkkeFunnetException;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlGruppe;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlHentIdenterRequest;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdenter;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.Oppholdstype;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlHentPersonRequest;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlPerson;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class PdlOppslagClientTest {

    private static final String OK_JSON = "/pdl/hentPersonOk.json";
    private static final String OK_UTEN_PERIODER_JSON = "/pdl/hentPersonOkUtenPerioder.json";
    private static final String FEIL_JSON = "/pdl/hentPersonError.json";
    private static final String PERSON_NOT_FOUND_JSON = "/pdl/hentPersonNotFound.json";
    private static final String HENT_IDENTER_OK = "/pdl/hentIdenterOk.json";

    private Provider<HttpServletRequest> requestProvider;

    @Before
    public void setUp() {
        requestProvider = mock(Provider.class);
    }

    @Test
    public void skalHenteOppholdTilPerson() {
        PdlOppslagClient client = new PdlOppslagClient("", null) {
            @Override
            String hentPersonRequest(String fnr, PdlHentPersonRequest request) {
                return toJson(OK_JSON);
            }
        };
        PdlPerson person = client.hentPerson(AktorId.of("444hhh"));

        Assert.assertEquals(Oppholdstype.MIDLERTIDIG, person.getOpphold().get(0).getType());
    }

    @Test
    public void skalHenteOppholdUtenPeriodeTilPerson() {
        PdlOppslagClient service = new PdlOppslagClient("", null) {
            @Override
            String hentPersonRequest(String fnr, PdlHentPersonRequest request) {
                return toJson(OK_UTEN_PERIODER_JSON);
            }
        };
        PdlPerson person = service.hentPerson(AktorId.of("444hhh"));

        Assert.assertEquals(Oppholdstype.PERMANENT, person.getOpphold().get(0).getType());
    }

    @Test(expected = RuntimeException.class)
    public void skalFeileVedError() {
        PdlOppslagClient pdlOppslagClient = new PdlOppslagClient("", null) {
            @Override
            String hentPersonRequest(String fnr, PdlHentPersonRequest request) {
                return toJson(FEIL_JSON);
            }
        };
        pdlOppslagClient.hentPerson(AktorId.of("111lll"));
    }

    @Test(expected = BrukerIkkeFunnetException.class)
    public void skalFeileVedNotFound() {
        PdlOppslagClient pdlOppslagClient = new PdlOppslagClient("", null) {
            @Override
            String hentPersonRequest(String fnr, PdlHentPersonRequest request) {
                return toJson(PERSON_NOT_FOUND_JSON);
            }
        };
        PdlPerson pdlPerson = pdlOppslagClient.hentPerson(AktorId.of("111lll"));
        assertThat(pdlPerson).isNull();
    }

    @Test
    public void skalHenteIdenterTilPerson() {
        PdlOppslagClient client = new PdlOppslagClient("", null) {
            @Override
            String hentIdenterRequest(String fnr, PdlHentIdenterRequest request) {
                return toJson(HENT_IDENTER_OK);
            }
        };

        PdlIdenter pdlIdenter = client.hentIdenter(Foedselsnummer.of("12345678910"));

        assertThat(pdlIdenter.getIdenter()).hasSize(2);
        assertTrue(pdlIdenter.getIdenter().stream()
                .anyMatch(pdlIdent -> pdlIdent.getGruppe() == PdlGruppe.FOLKEREGISTERIDENT && !pdlIdent.isHistorisk()));
        assertTrue(pdlIdenter.getIdenter().stream()
                .anyMatch(pdlIdent -> pdlIdent.getGruppe() == PdlGruppe.AKTORID && !pdlIdent.isHistorisk()));

    }

    private String toJson(String json_file) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(PdlOppslagClient.class.getResource(json_file).toURI()));
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
