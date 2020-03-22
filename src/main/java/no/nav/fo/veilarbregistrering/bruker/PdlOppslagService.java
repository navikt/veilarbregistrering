package no.nav.fo.veilarbregistrering.bruker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlPerson;
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlRequest;
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlResponse;
import no.nav.fo.veilarbregistrering.bruker.pdl.Variables;
import no.nav.fo.veilarbregistrering.oppgave.adapter.OppgaveRestClient;
import no.nav.log.MDCConstants;
import no.nav.sbl.rest.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Entity;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class PdlOppslagService {

    private final Logger logger = LoggerFactory.getLogger(PdlOppslagService.class);

    private final String NAV_CONSUMER_TOKEN_HEADER = "Nav-Consumer-Token";
    private final String NAV_PERSONIDENT_HEADER = "Nav-Personident";
    private final String NAV_CALL_ID_HEADER = "Nav-Call-Id";
    private final String TEMA_HEADER = "Tema";
    private final String ALLE_TEMA_HEADERVERDI = "GEN";
    private final String PDL_PROPERTY_NAME = "PDL_URL";

    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();

    public PdlPerson hentPerson(String fnr) {
        PdlRequest request = new PdlRequest(hentQuery(), new Variables(fnr, false));
        String json = pdlJson(fnr, request);
        PdlResponse resp = gson.fromJson(json, PdlResponse.class);
        validateResponse(resp);
        return resp.getData().getHentPerson();
    }

    String pdlJson(String fnr, PdlRequest request) {
        return RestUtils.withClient(client ->
                client.target(getRequiredProperty(PDL_PROPERTY_NAME))
                        .request()
                        .header(NAV_PERSONIDENT_HEADER, fnr)
                        .header(NAV_CALL_ID_HEADER, MDC.get(MDCConstants.MDC_CALL_ID))
                        .header("Authorization", "Bearer TODO_TOKEN")
                        .header(NAV_CONSUMER_TOKEN_HEADER, "Bearer TODO_TOKEN")
                        .header(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
                        .post(Entity.json(request), String.class));
    }

    private void validateResponse(PdlResponse response) {
        if (response.getErrors() != null && response.getErrors().size() > 0) {
            logger.warn("Error from PDL: " + gson.toJson(response.getErrors()));
            throw new RuntimeException("Error from PDL");
        }
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

    private static final class LocalDateAdapter extends TypeAdapter<LocalDate> {
        @Override
        public void write(final JsonWriter jsonWriter, final LocalDate localDate ) throws IOException {
            jsonWriter.value(localDate.toString());
        }

        @Override
        public LocalDate read( final JsonReader jsonReader ) throws IOException {
            return LocalDate.parse(jsonReader.nextString());
        }
    }
}
