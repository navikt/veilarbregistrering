package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.feil.Feil;
import no.nav.fo.veilarbregistrering.feil.FeilType;
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerResultat;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class OppfolgingClientMock extends OppfolgingClient {

    OppfolgingClientMock() {
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
    public AktiverBrukerResultat aktiverBruker(AktiverBrukerData fnr) {
        //sendException("BRUKER_ER_UKJENT");
        //sendException("BRUKER_KAN_IKKE_REAKTIVERES");
        //sendException("BRUKER_ER_DOD_UTVANDRET_ELLER_FORSVUNNET");
        //sendException("BRUKER_MANGLER_ARBEIDSTILLATELSE");
        return AktiverBrukerResultat.Companion.ok();
    }

    @Override
    public AktiverBrukerResultat reaktiverBruker(Foedselsnummer fnr) {
        return AktiverBrukerResultat.Companion.ok();
    }

    private void sendException(String feilType) {
        Feil feil = new Feil(
                new Feil.Type() {

                    @Override
                    public int getStatus() {
                        return 500;
                    }

                    @NotNull
                    @Override
                    public String getName() {
                        return feilType;
                    }
                }, "");
        throw new WebApplicationException(Response.serverError().entity(feil).build());
    }

}
