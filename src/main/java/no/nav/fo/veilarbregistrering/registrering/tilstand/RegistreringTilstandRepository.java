package no.nav.fo.veilarbregistrering.registrering.tilstand;

import java.util.List;
import java.util.Optional;

public interface RegistreringTilstandRepository {
    long lagre(RegistreringTilstand registreringTilstand);
    RegistreringTilstand oppdater(RegistreringTilstand registreringTilstand);
    RegistreringTilstand hentRegistreringTilstand(long id);
    List<RegistreringTilstand> finnRegistreringTilstanderMed(Status status);
    Optional<RegistreringTilstand> finnNesteRegistreringTilstandMed(Status status);
    int hentAntall(Status status);
    RegistreringTilstand hentTilstandFor(long registreringsId);
}
