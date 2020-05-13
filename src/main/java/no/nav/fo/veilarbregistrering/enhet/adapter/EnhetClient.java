package no.nav.fo.veilarbregistrering.enhet.adapter;

import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer;

import javax.ws.rs.core.Response;

import java.util.Optional;

import static javax.ws.rs.client.Entity.json;
import static no.nav.sbl.rest.RestUtils.RestConfig.builder;
import static no.nav.sbl.rest.RestUtils.withClient;

class EnhetClient {

    private static final int HTTP_READ_TIMEOUT = 120000;

    private final String baseUrl;

    EnhetClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Optional<OrganisasjonDetaljer> hentOrganisasjon(Organisasjonsnummer organisasjonsnummer) {
        String url = baseUrl + "/ereg/api/v1/organisasjon/";

        Response response = withClient(
                builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                client -> client
                        .target(url)
                        .request()
                        .post(json(organisasjonsnummer.asString())));

        Response.Status status = Response.Status.fromStatusCode(response.getStatus());

        if (status.equals(Response.Status.CREATED)) {
            return Optional.of(response.readEntity(OrganisasjonDetaljer.class));
        }

        if (status.equals(Response.Status.NOT_FOUND)) {
            return Optional.empty();
        }

        throw new RuntimeException("Hent organisasjon feilet med statuskode: " + status + " - " + response);
    }
}
