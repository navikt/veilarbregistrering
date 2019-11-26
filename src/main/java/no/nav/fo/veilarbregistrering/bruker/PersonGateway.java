package no.nav.fo.veilarbregistrering.bruker;

import java.util.Optional;

public interface PersonGateway {

    Optional<GeografiskTilknytning> hentGeografiskTilknytning(Foedselsnummer foedselsnummer);
}
