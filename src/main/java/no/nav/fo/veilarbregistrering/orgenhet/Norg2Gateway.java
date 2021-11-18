package no.nav.fo.veilarbregistrering.orgenhet;

import no.nav.fo.veilarbregistrering.enhet.Kommune;

import java.util.Map;
import java.util.Optional;

public interface Norg2Gateway {

    Optional<Enhetnr> hentEnhetFor(Kommune kommune);

    Map<Enhetnr, NavEnhet> hentAlleEnheter();
}
