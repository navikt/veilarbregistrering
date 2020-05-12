package no.nav.fo.veilarbregistrering.bruker.krr;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.KrrGateway;
import no.nav.fo.veilarbregistrering.bruker.KrrKontaktinfo;

class KrrGatewayImpl implements KrrGateway {

    private final KrrClient krrClient;

    KrrGatewayImpl(KrrClient krrClient) {
        this.krrClient = krrClient;
    }

    @Override
    public KrrKontaktinfo hentKontaktinfo(Bruker bruker) {
        KrrKontaktinfoDto kontaktinfoDto = krrClient.hentKontaktinfo(bruker.getFoedselsnummer());
        return map(kontaktinfoDto);
    }

    private KrrKontaktinfo map(KrrKontaktinfoDto kontaktinfoDto) {
        return KrrKontaktinfo.of(kontaktinfoDto.getMobiltelefonnummer());
    }
}
