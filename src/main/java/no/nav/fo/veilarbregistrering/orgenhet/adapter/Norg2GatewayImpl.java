package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetsnr;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;

import java.util.List;
import java.util.Optional;

class Norg2GatewayImpl implements Norg2Gateway {

    private final Norg2RestClient norg2RestClient;

    Norg2GatewayImpl(Norg2RestClient norg2RestClient) {
        this.norg2RestClient = norg2RestClient;
    }

    @Override
    public Optional<Enhetsnr> hentEnhetFor(Kommunenummer kommunenummer) {
        List<RsNavKontorDto> listeMedRsNavKontorDtos = norg2RestClient.hentEnhetFor(kommunenummer);

        return listeMedRsNavKontorDtos.stream()
                .filter(rsNavKontorDtos -> "Aktiv".equals(rsNavKontorDtos.getStatus()))
                .findFirst()
                .map(rsNavKontorDtos -> Enhetsnr.of(rsNavKontorDtos.getEnhetNr()));
    }
}
