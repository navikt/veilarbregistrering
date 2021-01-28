package no.nav.fo.veilarbregistrering.oppfolging.adapter;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerFeil;
import no.nav.fo.veilarbregistrering.registrering.bruker.AktiverBrukerResultat;
import no.nav.fo.veilarbregistrering.registrering.bruker.feil.AktiverBrukerException;

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

        throw new AktiverBrukerException(AktiverBrukerFeil.valueOf(feilType));
    }

}
