package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Bruker;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;

public interface ArbeidssokerRepository {

    long lagre(EndretFormidlingsgruppeCommand arenaFormidlingsgruppeEvent);

    Arbeidssokerperioder finnFormidlingsgrupper(Foedselsnummer foedselsnummer);

    Arbeidssokerperioder finnFormidlingsgrupper(Bruker bruker);
}
