package no.nav.fo.veilarbregistrering.registrering.tilstand;

import java.util.List;
import java.util.Optional;

public interface RegistreringTilstandRepository {
    long lagre(RegistreringTilstand registreringTilstand);
    void oppdater(RegistreringTilstand registreringTilstand);
    RegistreringTilstand hentRegistreringTilstand(long id);
    List<RegistreringTilstand> finnRegistreringTilstandMed(Status status);
    Optional<RegistreringTilstand> finnNesteRegistreringTilstandMed(Status status);
    int hentAntall(Status status);
    Optional<RegistreringTilstand> hentTilstandFor(long registreringsId);
}
