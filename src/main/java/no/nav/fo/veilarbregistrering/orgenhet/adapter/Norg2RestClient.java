package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

import static no.nav.fo.veilarbregistrering.orgenhet.adapter.RsArbeidsfordelingCriteriaDto.KONTAKT_BRUKER;
import static no.nav.fo.veilarbregistrering.orgenhet.adapter.RsArbeidsfordelingCriteriaDto.OPPFOLGING;
import static no.nav.sbl.rest.RestUtils.RestConfig.builder;
import static no.nav.sbl.rest.RestUtils.withClient;

class Norg2RestClient {

    private static final Logger LOG = LoggerFactory.getLogger(Norg2RestClient.class);

    private static final int HTTP_READ_TIMEOUT = 120000;

    private static final Gson gson = new GsonBuilder().create();

    private final String url;

    Norg2RestClient(String baseUrl) {
        this.url = baseUrl + "/v1/arbeidsfordeling/enheter/bestmatch";
    }

    List<RsNavKontorDto> hentEnhetFor(Kommunenummer kommunenummer) {
        RsArbeidsfordelingCriteriaDto rsArbeidsfordelingCriteriaDto = new RsArbeidsfordelingCriteriaDto();
        rsArbeidsfordelingCriteriaDto.setGeografiskOmraade(kommunenummer.asString());
        rsArbeidsfordelingCriteriaDto.setOppgavetype(KONTAKT_BRUKER);
        rsArbeidsfordelingCriteriaDto.setTema(OPPFOLGING);

        try {
            return utfoerRequest(rsArbeidsfordelingCriteriaDto);

        } catch (NotFoundException e) {
            LOG.warn("Fant ikke NavKontor for kommunenummer", e);
            return Collections.emptyList();

        } catch (RuntimeException e) {
            throw new RuntimeException("Hent NavKontor feilet.", e);
        }
    }

    List<RsNavKontorDto> utfoerRequest(RsArbeidsfordelingCriteriaDto rsArbeidsfordelingCriteriaDto) {
        Response response = withClient(
                builder().readTimeout(HTTP_READ_TIMEOUT).build(),
                client -> client
                        .property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true)
                        .target(url)
                        .request()
                        .method("GET", Entity.text(
                                toJson(rsArbeidsfordelingCriteriaDto))));

        return (List<RsNavKontorDto>) response.readEntity(List.class);
    }

    static String toJson(RsArbeidsfordelingCriteriaDto rsArbeidsfordelingCriteriaDto) {
        return gson.toJson(rsArbeidsfordelingCriteriaDto, RsArbeidsfordelingCriteriaDto.class);
    }
}