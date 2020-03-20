package no.nav.fo.veilarbregistrering.bruker;

import no.nav.fo.veilarbregistrering.bruker.pdl.PdlPerson;
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlRequest;
import no.nav.fo.veilarbregistrering.bruker.pdl.PdlResponse;
import no.nav.sbl.rest.RestUtils;

import javax.ws.rs.client.Entity;

public class PdlOppslagService {

    public static final String NAV_CONSUMER_TOKEN_HEADER = "Nav-Consumer-Token";
    public static final String NAV_PERSONIDENT_HEADER = "Nav-Personident";
    public static final String NAV_CALL_ID_HEADER = "Nav-Call-Id";
    public static final String TEMA_HEADER = "Tema";
    public static final String ALLE_TEMA_HEADERVERDI = "GEN";

    public PdlPerson hentPerson(String fnr) {
        // TODO Lag request, kall metode, returner
        return null;
    }

    private PdlResponse kall(String fnr, PdlRequest request) {

        String response = RestUtils.withClient(client ->
                client.target("PDL_URL, TODO")
                        .request()
                        .header(NAV_PERSONIDENT_HEADER, fnr)
                        .header(NAV_CALL_ID_HEADER, "TODO")
                        .header("Authorization", "Bearer TODO_TOKEN")
                        .header(NAV_CONSUMER_TOKEN_HEADER, "Bearer TODO_TOKEN")
                        .header(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
                        .post(Entity.json(request), String.class));

        return new PdlResponse(); // TODO Gjør om responsejson til objekt
    }
}
