package no.nav.fo.veilarbregistrering.bruker.krr;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Kontaktinfo;
import no.nav.fo.veilarbregistrering.bruker.KrrGateway;

class KrrGatewayImpl implements KrrGateway {

    private final KrrClient krrClient;

    KrrGatewayImpl(KrrClient krrClient) {
        this.krrClient = krrClient;
    }

    @Override
    public Kontaktinfo hentKontaktinfo(Bruker bruker) {
        KrrKontaktinfoDto kontaktinfoDto = krrClient.hentKontaktinfo(bruker.getFoedselsnummer());
        return map(kontaktinfoDto);
    }

    private Kontaktinfo map(KrrKontaktinfoDto kontaktinfoDto) {
        return Kontaktinfo.of(kontaktinfoDto.getMobiltelefonnummer());
    }
}
