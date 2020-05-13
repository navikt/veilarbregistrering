package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

import static javax.ws.rs.client.Entity.json;
import static no.nav.fo.veilarbregistrering.orgenhet.adapter.RsArbeidsfordelingCriteriaDto.KONTAKT_BRUKER;
import static no.nav.fo.veilarbregistrering.orgenhet.adapter.RsArbeidsfordelingCriteriaDto.OPPFOLGING;
import static no.nav.sbl.rest.RestUtils.RestConfig.builder;
import static no.nav.sbl.rest.RestUtils.withClient;

public class NorgRestClient {

    private static final int HTTP_READ_TIMEOUT = 120000;

    private final String baseUrl;

    public NorgRestClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<RsNavKontorDto> hentEnhetFor(Kommunenummer kommunenummer) {
        String url = baseUrl + "/norg2/api/v1/arbeidsfordeling/enheter/bestmatch";

        RsArbeidsfordelingCriteriaDto rsArbeidsfordelingCriteriaDto = new RsArbeidsfordelingCriteriaDto();
        rsArbeidsfordelingCriteriaDto.setGeografiskOmraade(kommunenummer.asString());
        rsArbeidsfordelingCriteriaDto.setOppgavetype(KONTAKT_BRUKER);
        rsArbeidsfordelingCriteriaDto.setTema(OPPFOLGING);

        Response response = withClient(
                builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                client -> client
                        .target(url)
                        .request()
                        .post(json(rsArbeidsfordelingCriteriaDto)));

        Response.Status status = Response.Status.fromStatusCode(response.getStatus());

        if (status.equals(Response.Status.CREATED)) {
            return  (List<RsNavKontorDto>) response.readEntity(List.class);
        }

        if (status.equals(Response.Status.NOT_FOUND)) {
            return Collections.emptyList();
        }

        throw new RuntimeException("Hent NavKontor feilet med statuskode: " + status + " - " + response);
    }
}
