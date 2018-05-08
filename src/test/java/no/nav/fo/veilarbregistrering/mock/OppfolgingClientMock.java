package no.nav.fo.veilarbregistrering.mock;

import no.nav.fo.veilarbregistrering.domain.AktiverBrukerData;
import no.nav.fo.veilarbregistrering.domain.OppfolgingStatus;
import no.nav.fo.veilarbregistrering.httpclient.OppfolgingClient;

import java.util.Optional;

import static java.util.Optional.of;

public class OppfolgingClientMock extends OppfolgingClient {

    public void aktiverBruker(AktiverBrukerData aktiverBrukerData) {
    }

    public Optional<OppfolgingStatus> hentOppfolgingsstatus(String fnr) {
        return of(new OppfolgingStatus().setInaktiveringsdato(null).setUnderOppfolging(false));
    }

}
