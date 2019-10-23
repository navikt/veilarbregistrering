package no.nav.fo.veilarbregistrering.bruker.adapter;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.fo.veilarbregistrering.bruker.GeografiskTilknytning;
import no.nav.fo.veilarbregistrering.bruker.PersonGateway;

import java.util.Optional;

public class PersonGatewayImpl implements PersonGateway {

    private final VeilArbPersonClient client;

    public PersonGatewayImpl(VeilArbPersonClient client) {
        this.client = client;
    }

    @Override
    public Optional<GeografiskTilknytning> hentGeografiskTilknytning(Foedselsnummer foedselsnummer) {
        return GeografiskTilknytning.ofNullable(client.geografisktilknytning(foedselsnummer));
    }
}
