package no.nav.fo.veilarbregistrering.arbeidssoker.adapter;

import com.google.gson.*;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.Periode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Supplier;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static no.nav.sbl.rest.RestUtils.RestConfig.builder;
import static no.nav.sbl.rest.RestUtils.withClient;

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
            FormidlingsgruppeResponseDto formidlingsgruppeResponseDto = parse(response);
            return Optional.of(formidlingsgruppeResponseDto);

        } catch (NotFoundException e) {
            LOG.warn("Søk på fødselsnummer gav ingen treff i Arena", e);
            return Optional.empty();

        } catch (RuntimeException e) {
            throw new RuntimeException("Hent formidlingshistorikk feilet", e);
        }
    }

    String utfoerRequest(Foedselsnummer foedselsnummer, Periode periode) {
        return withClient(
                builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                client -> client
                        .target(url)
                        .queryParam("fnr", foedselsnummer.stringValue())
                        .queryParam("fraDato", periode.fraDatoAs_yyyyMMdd())
                        //TODO: null-sjekk på tilDato - skal ikke alltid med
                        .queryParam("tilDato", periode.tilDatoAs_yyyyMMdd())
                        .request()
                        .header(AUTHORIZATION, "Bearer " + arenaOrdsTokenProvider.get())
                        .get(String.class));
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
