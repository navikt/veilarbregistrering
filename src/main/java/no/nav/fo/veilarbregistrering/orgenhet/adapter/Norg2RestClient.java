package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import no.nav.common.rest.client.RestClient;
import no.nav.common.rest.client.RestUtils;
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static no.nav.fo.veilarbregistrering.orgenhet.adapter.RsArbeidsfordelingCriteriaDto.KONTAKT_BRUKER;
import static no.nav.fo.veilarbregistrering.orgenhet.adapter.RsArbeidsfordelingCriteriaDto.OPPFOLGING;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

class Norg2RestClient {

    private static final Logger LOG = LoggerFactory.getLogger(Norg2RestClient.class);

    private final String baseUrl;

    Norg2RestClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    List<RsNavKontorDto> hentEnhetFor(Kommunenummer kommunenummer) {
        RsArbeidsfordelingCriteriaDto rsArbeidsfordelingCriteriaDto = new RsArbeidsfordelingCriteriaDto();
        rsArbeidsfordelingCriteriaDto.setGeografiskOmraade(kommunenummer.asString());
        rsArbeidsfordelingCriteriaDto.setOppgavetype(KONTAKT_BRUKER);
        rsArbeidsfordelingCriteriaDto.setTema(OPPFOLGING);

        Request request = new Request.Builder()
                .url(baseUrl + "/v1/arbeidsfordeling/enheter/bestmatch")
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .method("POST", RestUtils.toJsonRequestBody(rsArbeidsfordelingCriteriaDto))
                .build();

        try (Response response = RestClient.baseClient().newCall(request).execute()) {
            if (response.code() == 404) {
                LOG.warn("Fant ikke NavKontor for kommunenummer");
                return Collections.emptyList();
            }

            if (!response.isSuccessful()) {
                throw new RuntimeException("HentEnhetFor kommunenummer feilet med statuskode: " + response.code() + " - " + response);
            }

            List<RsNavKontorDto> rsNavKontorDtos = RestUtils.parseJsonResponseArrayOrThrow(response, RsNavKontorDto.class);
            return new ArrayList<>(rsNavKontorDtos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    List<RsEnhet> hentAlleEnheter() {
        Request request = new Request.Builder()
                .url(HttpUrl.parse(baseUrl).newBuilder()
                        .addPathSegments("v1/enhet")
                        .addQueryParameter("oppgavebehandlerFilter", "UFILTRERT").build())
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .build();

        try (Response response = RestClient.baseClient().newCall(request).execute()) {
            return RestUtils.parseJsonResponseArrayOrThrow(response, RsEnhet.class);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}