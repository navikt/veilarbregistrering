package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import no.nav.sbl.rest.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.client.Entity.json;
import static no.nav.fo.veilarbregistrering.orgenhet.adapter.RsArbeidsfordelingCriteriaDto.KONTAKT_BRUKER;
import static no.nav.fo.veilarbregistrering.orgenhet.adapter.RsArbeidsfordelingCriteriaDto.OPPFOLGING;

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

        Response response = utfoerRequest(rsArbeidsfordelingCriteriaDto);

        Response.Status status = Response.Status.fromStatusCode(response.getStatus());
        if (Response.Status.OK.equals(status)) {
            List<RsNavKontorDto> rsNavKontorDtos = response.readEntity(new GenericType<List<RsNavKontorDto>>() {
            });
            return rsNavKontorDtos.stream().collect(Collectors.toList());
        }

        if (Response.Status.NOT_FOUND.equals(status)) {
            LOG.warn("Fant ikke NavKontor for kommunenummer");
            return Collections.emptyList();
        }

        throw new RuntimeException("HentEnhetFor kommunenummer feilet med statuskode: " + status + " - " + response);
    }

    Response utfoerRequest(RsArbeidsfordelingCriteriaDto rsArbeidsfordelingCriteriaDto) {
        return RestUtils.createClient()
                        .target(baseUrl + "/v1/arbeidsfordeling/enheter/bestmatch")
                        .request(MediaType.APPLICATION_JSON)
                        .post(json(rsArbeidsfordelingCriteriaDto));
    }

    List<RsEnhet> hentAlleEnheter() {
        return RestUtils.createClient()
                .target(baseUrl + "/v1/enhet").queryParam("oppgavebehandlerFilter=UFILTRERT")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<RsEnhet>>() {
                });
    }
}