package no.nav.fo.veilarbregistrering.bruker.adapter;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning;
import no.nav.fo.veilarbregistrering.bruker.PersonGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


class PersonGatewayImpl implements PersonGateway {

    private final VeilArbPersonClient client;
    private final static Logger LOG = LoggerFactory.getLogger(PersonGatewayImpl.class);

    PersonGatewayImpl(VeilArbPersonClient client) {
        this.client = client;
    }

    @Override
    public Optional<GeografiskTilknytning> hentGeografiskTilknytning(Bruker bruker) {

        Optional<GeografiskTilknytning> geografiskTilknytningTPS = client.geografisktilknytning(bruker.getGjeldendeFoedselsnummer()).map(PersonGatewayImpl::map);

        return geografiskTilknytningTPS;
    }

    private static GeografiskTilknytning map(GeografiskTilknytningDto dto) {
        return GeografiskTilknytning.of(dto.getGeografiskTilknytning());
    }
}
