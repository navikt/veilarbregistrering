package no.nav.fo.veilarbregistrering.bruker;

import java.util.Optional;

public interface KrrGateway {

    Optional<Telefonnummer> hentKontaktinfo(Bruker bruker);
}
