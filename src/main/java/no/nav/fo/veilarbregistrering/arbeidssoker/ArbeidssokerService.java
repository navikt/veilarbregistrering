package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.db.arbeidssoker.ArenaFormidlingsgruppeEvent;

public class ArbeidssokerService {

    ArbeidssokerRepository arbeidssokerRepository;

    public ArbeidssokerService(ArbeidssokerRepository arbeidssokerRepository) {
        this.arbeidssokerRepository = arbeidssokerRepository;
    }

    public long lagreFormidlingsgruppe(ArenaFormidlingsgruppeEvent arenaFormidlingsgruppeEvent) {
        return arbeidssokerRepository.lagre(arenaFormidlingsgruppeEvent);
    }
}
