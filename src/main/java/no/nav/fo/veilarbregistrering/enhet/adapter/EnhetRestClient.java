package no.nav.fo.veilarbregistrering.enhet.adapter;

import com.google.gson.*;
import no.nav.common.rest.client.RestClient;
import no.nav.common.rest.client.RestUtils;
import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


class EnhetRestClient {

    private static final long HTTP_READ_TIMEOUT = 120000;

    private static final Logger LOG = LoggerFactory.getLogger(EnhetRestClient.class);

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateDeserializer()).create();

    private final String url;
    private final OkHttpClient client;

    EnhetRestClient(String baseUrl) {
        this.url = baseUrl + "/v1/organisasjon/";
        client = RestClient.baseClientBuilder().readTimeout(HTTP_READ_TIMEOUT, MILLISECONDS).build();
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
        Request request = new Request.Builder()
                .url(this.url + organisasjonsnummer.asString())
                .build();
        try {
            Response response = client.newCall(request).execute();
            RestUtils.throwIfNotSuccessful(response);
            return RestUtils.getBodyStr(response).orElseThrow(RuntimeException::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
