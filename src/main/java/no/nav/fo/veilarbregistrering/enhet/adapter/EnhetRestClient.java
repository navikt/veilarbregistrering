package no.nav.fo.veilarbregistrering.enhet.adapter;

import com.google.gson.*;
import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Optional;

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
        try {
            String response = utfoerRequest(organisasjonsnummer);
            OrganisasjonDto organisasjonDto = parse(response);
            return Optional.of(organisasjonDto.getOrganisasjonDetaljer());

        } catch (NotFoundException e) {
            LOG.warn("Fant ikke organisasjon for organisasjonsnummer", e);
            return Optional.empty();

        } catch (RuntimeException e) {
            throw new RuntimeException("Hent organisasjon feilet", e);
        }
    }

    String utfoerRequest(Organisasjonsnummer organisasjonsnummer) {
        return withClient(
                builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                client -> client
                        .target(url + organisasjonsnummer.asString())
                        .request()
                        .get(String.class));
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
