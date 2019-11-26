package no.nav.fo.veilarbregistrering.bruker.adapter;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning;
import no.nav.fo.veilarbregistrering.bruker.PersonGateway;

import java.util.Optional;

class PersonGatewayImpl implements PersonGateway {

    private final VeilArbPersonClient client;

    PersonGatewayImpl(VeilArbPersonClient client) {
        this.client = client;
    }

    @Override
    public Optional<GeografiskTilknytning> hentGeografiskTilknytning(Foedselsnummer foedselsnummer) {
        return client.geografisktilknytning(foedselsnummer).map(PersonGatewayImpl::map);
    }

    private static GeografiskTilknytning map(GeografiskTilknytningDto dto) {
        return GeografiskTilknytning.of(dto.getGeografiskTilknytning());
    }
}
