package no.nav.fo.veilarbregistrering.bruker.adapter;

import no.nav.common.rest.client.RestClient;
import no.nav.common.rest.client.RestUtils;
import no.nav.common.sts.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static no.nav.fo.veilarbregistrering.config.RequestContext.servletRequest;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;


class VeilArbPersonClient {

    private static final Logger LOG = LoggerFactory.getLogger(VeilArbPersonClient.class);

    private static final int HTTP_READ_TIMEOUT = 120000;

    private final String baseUrl;
    private final SystemUserTokenProvider systemUserTokenProvider;
    private final OkHttpClient client;

    VeilArbPersonClient(String baseUrl, SystemUserTokenProvider systemUserTokenProvider) {
        this.baseUrl = baseUrl;
        this.systemUserTokenProvider = systemUserTokenProvider;
        this.client = RestClient.baseClientBuilder().readTimeout(HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS).build();
    }

    Optional<GeografiskTilknytningDto> geografisktilknytning(Foedselsnummer foedselsnummer) {
        String cookies = servletRequest().getHeader(COOKIE);
        try {
            Request request = new Request.Builder()
                    .url(HttpUrl.parse(baseUrl).newBuilder()
                            .addPathSegments("person/geografisktilknytning")
                            .addQueryParameter("fnr", foedselsnummer.stringValue())
                            .build())
                    .header(COOKIE, cookies)
                    .header("SystemAuthorization", this.systemUserTokenProvider.getSystemUserToken())
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                if (response.code() == NOT_FOUND.value()) {
                    LOG.warn("Fant ikke geografisk tilknytning for bruker.");
                    return Optional.empty();
                } else if (response.code() == FORBIDDEN.value()) {
                    throw new RuntimeException("Bruker har ikke tilgang på å hente ut geografisk tilknytning fra VeilArbPerson-tjenesten.");
                } else {
                    throw new RuntimeException("Feil ved kall til VeilArbPerson-tjenesten.");
                }
            }
            GeografiskTilknytningDto geografiskTilknytningDto = RestUtils.parseJsonResponseOrThrow(response, GeografiskTilknytningDto.class);

            return geografiskTilknytningDto != null && geografiskTilknytningDto.getGeografiskTilknytning() != null
                    ? Optional.of(geografiskTilknytningDto) : Optional.empty();
        } catch (IOException e) {
            throw new RuntimeException("Feil ved kall til VeilArbPerson-tjenesten.", e);
        }
    }
}
