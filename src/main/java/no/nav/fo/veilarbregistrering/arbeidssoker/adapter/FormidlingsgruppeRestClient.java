package no.nav.fo.veilarbregistrering.arbeidssoker.adapter;

import com.google.gson.*;
import no.nav.common.rest.client.RestClient;
import no.nav.common.rest.client.RestUtils;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.Periode;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

class FormidlingsgruppeRestClient {

    private static final int HTTP_READ_TIMEOUT = 120000;

    private static final Logger LOG = LoggerFactory.getLogger(FormidlingsgruppeRestClient.class);

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateDeserializer()).create();

    private final String url;
    private final Supplier<String> arenaOrdsTokenProvider;

    FormidlingsgruppeRestClient(String baseUrl, Supplier<String> arenaOrdsTokenProvider) {
        this.url = baseUrl + "/v1/person/arbeidssoeker/formidlingshistorikk";
        this.arenaOrdsTokenProvider = arenaOrdsTokenProvider;
    }

    Optional<FormidlingsgruppeResponseDto> hentFormidlingshistorikk(Foedselsnummer foedselsnummer, Periode periode) {
        try {
            String response = utfoerRequest(foedselsnummer, periode);
            if (response == null) {
                LOG.warn("Søk på fødselsnummer gav ingen treff i Arena");
                return Optional.empty();
            }
            FormidlingsgruppeResponseDto formidlingsgruppeResponseDto = parse(response);
            return Optional.of(formidlingsgruppeResponseDto);

        } catch (RuntimeException e) {
            throw new RuntimeException("Hent formidlingshistorikk feilet", e);
        }
    }

    @Nullable
    private String utfoerRequest(Foedselsnummer foedselsnummer, Periode periode) {
        Request request = new Request.Builder()
                .url(HttpUrl.parse(url).newBuilder()
                        .addQueryParameter("fnr", foedselsnummer.stringValue())
                        .addQueryParameter("fraDato", periode.fraDatoAs_yyyyMMdd())
                        //TODO: null-sjekk på tilDato - skal ikke alltid med
                        .addQueryParameter("tilDato", periode.tilDatoAs_yyyyMMdd())
                        .build())
                .build();

        OkHttpClient httpClient = RestClient.baseClient().newBuilder().readTimeout(HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS).build();
        try (Response response = httpClient.newCall(request).execute()){
            if (response.code() == HttpStatus.NOT_FOUND.value()) return null;
            else if (!response.isSuccessful()) throw new RuntimeException("Feilkode: " + response.code());
            return RestUtils.getBodyStr(response).orElseThrow(() -> new RuntimeException("Feil ved uthenting av response body"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static FormidlingsgruppeResponseDto parse(String jsonResponse) {
        return GSON.fromJson(jsonResponse, FormidlingsgruppeResponseDto.class);
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
