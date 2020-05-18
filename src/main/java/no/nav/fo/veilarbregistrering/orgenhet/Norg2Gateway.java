package no.nav.fo.veilarbregistrering.orgenhet;

import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;

import java.util.Optional;

public interface Norg2Gateway {

    Optional<Enhetsnr> hentEnhetFor(Kommunenummer kommunenummer);
}
