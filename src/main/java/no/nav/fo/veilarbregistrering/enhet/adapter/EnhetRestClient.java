package no.nav.fo.veilarbregistrering.enhet.adapter;

import com.google.gson.*;
import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Optional;

import static javax.ws.rs.client.Entity.json;
import static no.nav.sbl.rest.RestUtils.RestConfig.builder;
import static no.nav.sbl.rest.RestUtils.withClient;

class EnhetRestClient {

    private static final int HTTP_READ_TIMEOUT = 120000;

    private static final Logger LOG = LoggerFactory.getLogger(EnhetRestClient.class);

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateDeserializer()).create();

    private final String url;

    EnhetRestClient(String baseUrl) {
        this.url = baseUrl + "/v1/organisasjon/";
    }

    Optional<OrganisasjonDetaljerDto> hentOrganisasjon(Organisasjonsnummer organisasjonsnummer) {
        Response response = withClient(
                builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                client -> client
                        .target(url)
                        .request()
                        .post(json(organisasjonsnummer.asString())));

        Response.Status status = Response.Status.fromStatusCode(response.getStatus());
        LOG.info("Statuskode: ", status.getStatusCode());

        if (status.equals(Response.Status.OK)) {
            String jsonResponse = response.readEntity(String.class);
            OrganisasjonDto organisasjonDto = parse(jsonResponse);
            return Optional.of(organisasjonDto.getOrganisasjonDetaljer());
        }

        if (status.equals(Response.Status.NOT_FOUND)) {
            return Optional.empty();
        }

        throw new RuntimeException("Hent organisasjon feilet med statuskode: " + status + " - " + response);
    }

    static OrganisasjonDto parse(String jsonResponse) {
        return gson.fromJson(jsonResponse, OrganisasjonDto.class);
    }

    private static class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {

            return Optional.ofNullable(json.getAsJsonPrimitive().getAsString())
                    .map(LocalDate::parse)
                    .orElse(null);
        }
    }
}
