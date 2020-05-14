package no.nav.fo.veilarbregistrering.bruker.krr;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.KrrGateway;
import no.nav.fo.veilarbregistrering.bruker.Telefonnummer;

import java.util.Optional;

class KrrGatewayImpl implements KrrGateway {

    private final KrrClient krrClient;

    KrrGatewayImpl(KrrClient krrClient) {
        this.krrClient = krrClient;
    }

    @Override
    public Optional<Telefonnummer> hentKontaktinfo(Bruker bruker) {
        return krrClient.hentKontaktinfo(bruker.getFoedselsnummer())
                .map(kontakt -> Telefonnummer.of(kontakt.getMobiltelefonnummer()));
    }
}
