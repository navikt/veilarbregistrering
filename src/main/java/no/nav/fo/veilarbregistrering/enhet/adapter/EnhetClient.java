package no.nav.fo.veilarbregistrering.enhet.adapter;

import no.nav.common.oidc.SystemUserTokenProvider;
import no.nav.fo.veilarbregistrering.arbeidsforhold.Organisasjonsnummer;

import javax.ws.rs.core.Response;

import static javax.ws.rs.client.Entity.json;
import static no.nav.sbl.rest.RestUtils.RestConfig.builder;
import static no.nav.sbl.rest.RestUtils.withClient;

class EnhetClient {

    private static final int HTTP_READ_TIMEOUT = 120000;

    private final String baseUrl;

    EnhetClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public OrganisasjonDetaljer hentOrganisasjon(Organisasjonsnummer organisasjonsnummer) {
        String url = baseUrl + "/ereg/api/v1/organisasjon/";

        Response response = withClient(
                builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                client -> client
                        .target(url)
                        .request()
                        //TODO: Trenger vi header?
                        .post(json(organisasjonsnummer)));

        //TODO: Feilhåndtering og sjekk mot statuskoder

        return response.readEntity(OrganisasjonDetaljer.class);
    }

}
