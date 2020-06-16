package no.nav.fo.veilarbregistrering.registrering.bruker;

import java.util.List;
import java.util.Optional;

public interface AktiveringTilstandRepository {
    long lagre(AktiveringTilstand registreringTilstand);
    void oppdater(AktiveringTilstand aktiveringTilstand);
    AktiveringTilstand hentAktiveringTilstand(long id);
    List<AktiveringTilstand> finnAktiveringTilstandMed(Status status);
    Optional<AktiveringTilstand> finnNesteAktiveringTilstandForOverforing();
    Optional<AktiveringTilstand> finnNesteAktiveringTilstandSomHarFeilet();
}
