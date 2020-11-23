package no.nav.fo.veilarbregistrering.registrering.bruker;

import no.nav.fo.veilarbregistrering.registrering.resources.RegistreringTilstandDto;

import java.util.List;

public class AktiveringTilstandService {

    private final AktiveringTilstandRepository aktiveringTilstandRepository;

    public AktiveringTilstandService(AktiveringTilstandRepository aktiveringTilstandRepository) {
        this.aktiveringTilstandRepository = aktiveringTilstandRepository;
    }

    public void oppdaterRegistreringTilstand(RegistreringTilstandDto registreringTilstandDto) {
        AktiveringTilstand original = aktiveringTilstandRepository.hentAktiveringTilstand(registreringTilstandDto.getId());
        AktiveringTilstand oppdatert = original.oppdaterStatus(registreringTilstandDto.getStatus());
        aktiveringTilstandRepository.oppdater(oppdatert);
    }

    public List<AktiveringTilstand> finnAktiveringTilstandMed(Status status) {
        return aktiveringTilstandRepository.finnAktiveringTilstandMed(status);
    }

}
