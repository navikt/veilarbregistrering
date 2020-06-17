package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.db.arbeidssoker.ArenaFormidlingsgruppeEvent;

public interface ArbeidssokerRepository {
    long lagre(ArenaFormidlingsgruppeEvent arenaFormidlingsgruppeEvent);
}
