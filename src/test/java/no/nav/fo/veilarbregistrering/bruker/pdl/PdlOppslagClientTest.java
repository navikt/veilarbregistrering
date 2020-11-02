package no.nav.fo.veilarbregistrering.bruker.pdl;

import no.nav.fo.veilarbregistrering.bruker.AktorId;
import no.nav.fo.veilarbregistrering.bruker.BrukerIkkeFunnetException;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlGruppe;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlHentIdenterRequest;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentIdenter.PdlIdenter;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlHentPersonRequest;
import no.nav.fo.veilarbregistrering.bruker.pdl.hentPerson.PdlPerson;
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

    private static final String HENT_PERSON_FEIL_JSON = "/pdl/hentPersonError.json";
    private static final String HENT_PERSON_NOT_FOUND_JSON = "/pdl/hentPersonNotFound.json";
    private static final String HENT_IDENTER_OK_JSON = "/pdl/hentIdenterOk.json";
    private static final String HENT_IDENTER_MED_HISTORISK_OK_JSON = "/pdl/hentIdenterMedHistorikkOk.json";

    private Provider<HttpServletRequest> requestProvider;

    @Before
    public void setUp() {
        requestProvider = mock(Provider.class);
    }

    @Test(expected = RuntimeException.class)
    public void skalFeileVedError() {
        PdlOppslagClient pdlOppslagClient = new PdlOppslagClient("", null) {
            @Override
            String hentPersonRequest(String fnr, PdlHentPersonRequest request) {
                return toJson(HENT_PERSON_FEIL_JSON);
            }
        };
        pdlOppslagClient.hentPerson(AktorId.of("111lll"));
    }

    @Test(expected = BrukerIkkeFunnetException.class)
    public void skalFeileVedNotFound() {
        PdlOppslagClient pdlOppslagClient = new PdlOppslagClient("", null) {
            @Override
            String hentPersonRequest(String fnr, PdlHentPersonRequest request) {
                return toJson(HENT_PERSON_NOT_FOUND_JSON);
            }
        };
        PdlPerson pdlPerson = pdlOppslagClient.hentPerson(AktorId.of("111lll"));
        assertThat(pdlPerson).isNull();
    }

    @Test
    public void skalHenteIdenterTilPerson() {
        PdlOppslagClient client = new PdlOppslagClient("", null) {
            @Override
            String hentIdenterRequest(String personident, PdlHentIdenterRequest request) {
                return toJson(HENT_IDENTER_OK_JSON);
            }
        };

        PdlIdenter pdlIdenter = client.hentIdenter(Foedselsnummer.of("12345678910"));

        assertThat(pdlIdenter.getIdenter()).hasSize(2);
        assertTrue(pdlIdenter.getIdenter().stream()
                .anyMatch(pdlIdent -> pdlIdent.getGruppe() == PdlGruppe.FOLKEREGISTERIDENT && !pdlIdent.isHistorisk()));
        assertTrue(pdlIdenter.getIdenter().stream()
                .anyMatch(pdlIdent -> pdlIdent.getGruppe() == PdlGruppe.AKTORID && !pdlIdent.isHistorisk()));

    }

    @Test
    public void skalHenteIdenterMedHistorikkTilPerson() {
        PdlOppslagClient client = new PdlOppslagClient("", null) {
            @Override
            String hentIdenterRequest(String personident, PdlHentIdenterRequest request) {
                return toJson(HENT_IDENTER_MED_HISTORISK_OK_JSON);
            }
        };

        PdlIdenter pdlIdenter = client.hentIdenter(Foedselsnummer.of("12345678910"));

        assertThat(pdlIdenter.getIdenter()).hasSize(3);
        assertTrue(pdlIdenter.getIdenter().stream()
                .anyMatch(pdlIdent -> pdlIdent.getGruppe() == PdlGruppe.FOLKEREGISTERIDENT && !pdlIdent.isHistorisk()));
        assertTrue(pdlIdenter.getIdenter().stream()
                .anyMatch(pdlIdent -> pdlIdent.getGruppe() == PdlGruppe.AKTORID && !pdlIdent.isHistorisk()));
        assertTrue(pdlIdenter.getIdenter().stream()
                .anyMatch(pdlIdent -> pdlIdent.getGruppe() == PdlGruppe.FOLKEREGISTERIDENT && pdlIdent.isHistorisk()));

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
