package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr;
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

class Norg2GatewayImpl implements Norg2Gateway {

    private final Norg2RestClient norg2RestClient;

    Norg2GatewayImpl(Norg2RestClient norg2RestClient) {
        this.norg2RestClient = norg2RestClient;
    }

    @Override
    public Optional<Enhetnr> hentEnhetFor(Kommunenummer kommunenummer) {
        List<RsNavKontorDto> listeMedRsNavKontorDtos = norg2RestClient.hentEnhetFor(kommunenummer);

        return listeMedRsNavKontorDtos.stream()
                .filter(rsNavKontorDtos -> "Aktiv".equals(rsNavKontorDtos.getStatus()))
                .findFirst()
                .map(rsNavKontorDtos -> Enhetnr.of(rsNavKontorDtos.getEnhetNr()));
    }

    public Map<Enhetnr, NavEnhet> hentAlleEnheter() {
        List<RsEnhet> rsEnhets = norg2RestClient.hentAlleEnheter();

        return rsEnhets.stream()
                .map(rs -> new NavEnhet(Enhetnr.of(rs.getEnhetNr()), rs.getNavn()))
                .collect(toMap(NavEnhet::getId, navEnhet -> navEnhet));
    }
}
