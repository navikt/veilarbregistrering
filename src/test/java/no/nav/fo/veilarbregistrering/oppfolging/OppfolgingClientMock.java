package no.nav.fo.veilarbregistrering.oppfolging;

import no.nav.apiapp.feil.FeilDTO;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.AktiverBrukerData;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingStatusData;
import no.nav.fo.veilarbregistrering.oppfolging.adapter.OppfolgingClient;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class OppfolgingClientMock extends OppfolgingClient {

    public OppfolgingClientMock() {
        super(null, null);
    }

    @Override
    public OppfolgingStatusData hentOppfolgingsstatus(Foedselsnummer fnr) {
        return new OppfolgingStatusData()
                .withUnderOppfolging(false)
                .withKanReaktiveres(false)
                .withErSykmeldtMedArbeidsgiver(true);
    }

    @Override
    public void aktiverBruker(AktiverBrukerData fnr) {
        //sendException("BRUKER_ER_UKJENT");
        //sendException("BRUKER_KAN_IKKE_REAKTIVERES");
        //sendException("BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET");
        //sendException("BRUKER_MANGLER_ARBEIDSTILLATELSE");
    }

    @Override
    public void reaktiverBruker(Foedselsnummer fnr) {

    }

    private void sendException(String feilType) {
        FeilDTO feilDTO = new FeilDTO("1", feilType, new FeilDTO.Detaljer(feilType, "", ""));
        throw new WebApplicationException(Response.serverError().entity(feilDTO).build());
    }

}
