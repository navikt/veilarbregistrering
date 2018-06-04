package no.nav.fo.veilarbregistrering.mock;

import no.nav.fo.veilarbregistrering.domain.AktivStatus;
import no.nav.fo.veilarbregistrering.domain.AktiverBrukerData;
import no.nav.fo.veilarbregistrering.domain.AktiverBrukerResponseStatus;
import no.nav.fo.veilarbregistrering.httpclient.OppfolgingClient;

import static no.nav.fo.veilarbregistrering.domain.AktiverBrukerResponseStatus.Status.STATUS_SUKSESS;

public class OppfolgingClientMock extends OppfolgingClient {

    public OppfolgingClientMock() {
        super(null);
    }

    public AktiverBrukerResponseStatus aktiverBruker(AktiverBrukerData aktiverBrukerData) {
        return new AktiverBrukerResponseStatus(STATUS_SUKSESS);
    }

    public AktivStatus hentOppfolgingsstatus(String fnr) {
        return new AktivStatus().withInaktiveringDato(null).withUnderOppfolging(false).withAktiv(false);
    }

}
