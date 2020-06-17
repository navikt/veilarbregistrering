package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.kafka.FormidlingsgruppeEvent;

public interface ArbeidssokerRepository {
    long lagre(FormidlingsgruppeEvent arenaFormidlingsgruppe);
}
