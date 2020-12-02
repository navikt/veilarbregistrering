package no.nav.fo.veilarbregistrering.registrering.tilstand;

import no.nav.fo.veilarbregistrering.registrering.tilstand.resources.RegistreringTilstandDto;

import java.util.List;

public class RegistreringTilstandService {

    private final RegistreringTilstandRepository registreringTilstandRepository;

    public RegistreringTilstandService(RegistreringTilstandRepository registreringTilstandRepository) {
        this.registreringTilstandRepository = registreringTilstandRepository;
    }

    public void oppdaterRegistreringTilstand(RegistreringTilstandDto registreringTilstandDto) {
        RegistreringTilstand original = registreringTilstandRepository.hentRegistreringTilstand(registreringTilstandDto.getId());
        RegistreringTilstand oppdatert = original.oppdaterStatus(registreringTilstandDto.getStatus());
        registreringTilstandRepository.oppdater(oppdatert);
    }

    public List<RegistreringTilstand> finnRegistreringTilstandMed(Status status) {
        return registreringTilstandRepository.finnRegistreringTilstandMed(status);
    }

}
