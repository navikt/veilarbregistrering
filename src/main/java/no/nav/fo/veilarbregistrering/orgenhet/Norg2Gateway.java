package no.nav.fo.veilarbregistrering.orgenhet;

import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;

import java.util.Map;
import java.util.Optional;

public interface Norg2Gateway {

    Optional<Enhetnr> hentEnhetFor(Kommunenummer kommunenummer);

    Map<Enhetnr, NavEnhet> hentAlleEnheter();
}
