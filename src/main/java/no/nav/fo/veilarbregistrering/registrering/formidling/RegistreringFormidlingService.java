package no.nav.fo.veilarbregistrering.registrering.formidling;

import java.util.List;

public class RegistreringFormidlingService {

    private final RegistreringFormidlingRepository registreringFormidlingRepository;

    public RegistreringFormidlingService(RegistreringFormidlingRepository registreringFormidlingRepository) {
        this.registreringFormidlingRepository = registreringFormidlingRepository;
    }

    public void oppdaterRegistreringTilstand(OppdaterRegistreringFormidlingCommand command) {
        RegistreringFormidling original = registreringFormidlingRepository.hentRegistreringTilstand(command.getId());
        RegistreringFormidling oppdatert = original.oppdaterStatus(command.getStatus());
        registreringFormidlingRepository.oppdater(oppdatert);
    }

    public List<RegistreringFormidling> finnRegistreringTilstandMed(Status status) {
        return registreringFormidlingRepository.finnRegistreringTilstanderMed(status);
    }

}
