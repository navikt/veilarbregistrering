package no.nav.fo.veilarbregistrering.registrering.tilstand;

import java.util.List;

public class RegistreringTilstandService {

    private final RegistreringTilstandRepository registreringTilstandRepository;

    public RegistreringTilstandService(RegistreringTilstandRepository registreringTilstandRepository) {
        this.registreringTilstandRepository = registreringTilstandRepository;
    }

    public void oppdaterRegistreringTilstand(OppdaterRegistreringTilstandCommand command) {
        RegistreringTilstand original = registreringTilstandRepository.hentRegistreringTilstand(command.getId());
        RegistreringTilstand oppdatert = original.oppdaterStatus(command.getStatus());
        registreringTilstandRepository.oppdater(oppdatert);
    }

    public List<RegistreringTilstand> finnRegistreringTilstandMed(Status status) {
        return registreringTilstandRepository.finnRegistreringTilstanderMed(status);
    }

}
