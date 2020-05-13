package no.nav.fo.veilarbregistrering.bruker.krr;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.KrrGateway;
import no.nav.fo.veilarbregistrering.bruker.Telefonnummer;

class KrrGatewayImpl implements KrrGateway {

    private final KrrClient krrClient;

    KrrGatewayImpl(KrrClient krrClient) {
        this.krrClient = krrClient;
    }

    @Override
    public Telefonnummer hentKontaktinfo(Bruker bruker) {
        return Telefonnummer.of(krrClient.hentKontaktinfo(bruker.getFoedselsnummer()).getMobiltelefonnummer());
    }

}
