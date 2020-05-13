package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetsnr;
import no.nav.fo.veilarbregistrering.orgenhet.NorgGateway;

import java.util.List;
import java.util.Optional;

class NorgGatewayImpl implements NorgGateway {

    private final NorgRestClient norgRestClient;

    NorgGatewayImpl(NorgRestClient norgRestClient) {
        this.norgRestClient = norgRestClient;
    }

    @Override
    public Optional<Enhetsnr> hentEnhetFor(Kommunenummer kommunenummer) {
        List<RsNavKontorDto> listeMedRsNavKontorDtos = norgRestClient.hentEnhetFor(kommunenummer);

        return listeMedRsNavKontorDtos.stream()
                //TODO: Trenger vi noe mer filter her? Arbeidsgiver sjekker på Status = Aktiv???
                .findFirst()
                .map(rsNavKontorDtos -> Enhetsnr.of(rsNavKontorDtos.getEnhetId()));
    }
}
