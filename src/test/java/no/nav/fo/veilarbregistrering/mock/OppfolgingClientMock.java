package no.nav.fo.veilarbregistrering.mock;

import no.nav.fo.veilarbregistrering.domain.AktivStatus;
import no.nav.fo.veilarbregistrering.domain.AktiverBrukerData;
import no.nav.fo.veilarbregistrering.httpclient.OppfolgingClient;

public class OppfolgingClientMock extends OppfolgingClient {

    public OppfolgingClientMock() {
        super(null);
    }

    @Override
    public AktivStatus hentOppfolgingsstatus(String fnr) {
        return new AktivStatus().withInaktiveringDato(null).withUnderOppfolging(false).withAktiv(false);
    }

    @Override
    public void aktiverBruker(AktiverBrukerData fnr) {
    }

}
