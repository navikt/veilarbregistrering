package no.nav.fo.veilarbregistrering.mock;

import no.nav.fo.veilarbregistrering.domain.AktivStatus;
import no.nav.fo.veilarbregistrering.httpclient.OppfolgingClient;

public class OppfolgingClientMock extends OppfolgingClient {

    public OppfolgingClientMock() {
        super(null);
    }


    public AktivStatus hentOppfolgingsstatus(String fnr) {
        return new AktivStatus().withInaktiveringDato(null).withUnderOppfolging(false).withAktiv(false);
    }

}
