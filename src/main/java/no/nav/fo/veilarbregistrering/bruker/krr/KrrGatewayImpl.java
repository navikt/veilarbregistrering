package no.nav.fo.veilarbregistrering.bruker.krr;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Kontaktinfo;
import no.nav.fo.veilarbregistrering.bruker.KrrGateway;

public class KrrGatewayImpl implements KrrGateway {

    private final KrrClient krrClient;

    public KrrGatewayImpl(KrrClient krrClient) {
        this.krrClient = krrClient;
    }

    @Override
    public Kontaktinfo hentKontaktinfo(Bruker bruker) {
        KontaktinfoDto kontaktinfoDto = krrClient.hentKontaktinfo(bruker.getFoedselsnummer());
        return map(kontaktinfoDto);
    }

    private Kontaktinfo map(KontaktinfoDto kontaktinfoDto) {
        return Kontaktinfo.of(
                kontaktinfoDto.getEpostadresse(),
                kontaktinfoDto.getMobiltelefonnummer());
    }
}
