package no.nav.fo.veilarbregistrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.pdl.PdlPerson;
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlRequest;
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlResponse;
import no.nav.fo.veilarbregistrering.bruker.pdl.Variables;
import no.nav.fo.veilarbregistrering.oppgave.adapter.OppgaveRestClient;
import no.nav.sbl.rest.RestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Entity;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class PdlOppslagService {

    /*
        - name: PDL_BASE_URL
    value: http://pdl-api.default.svc.nais.local/graphql*/


    @Value("${pdl.url}")
    private String pdlEndpointUrl;

    @Value("${application.name}")
    private String srvUserName;

    public static final String NAV_CONSUMER_TOKEN_HEADER = "Nav-Consumer-Token";
    public static final String NAV_PERSONIDENT_HEADER = "Nav-Personident";
    public static final String NAV_CALL_ID_HEADER = "Nav-Call-Id";
    public static final String TEMA_HEADER = "Tema";
    public static final String ALLE_TEMA_HEADERVERDI = "GEN";
    public static final String PDL_PROPERTY_NAME = "PDL_URL";

    public PdlPerson hentPerson(String fnr) {
        PdlRequest request = new PdlRequest(hentQuery(), new Variables(fnr, false));
        return kall(fnr, request).getData().getHentPerson();
    }

    private PdlResponse kall(String fnr, PdlRequest request) {


        String response = RestUtils.withClient(client ->
                client.target(getRequiredProperty(PDL_PROPERTY_NAME))
                        .request()
                        .header(NAV_PERSONIDENT_HEADER, fnr)
                        .header(NAV_CALL_ID_HEADER, "TODO")
                        .header("Authorization", "Bearer TODO_TOKEN")
                        .header(NAV_CONSUMER_TOKEN_HEADER, "Bearer TODO_TOKEN")
                        .header(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
                        .post(Entity.json(request), String.class));

        return new PdlResponse(); // TODO Gjør om responsejson til objekt
    }

    private String hentQuery() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(PdlOppslagService.class.getResource("/pdl/hentPerson.graphql").toURI()));
            return new String(bytes).replaceAll("[\n\r]]", "");
        } catch (IOException e) {
           throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
