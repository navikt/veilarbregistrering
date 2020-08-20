package no.nav.fo.veilarbregistrering.arbeidssoker;

import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;

import java.util.List;

public interface ArbeidssokerRepository {

    long lagre(EndretFormidlingsgruppeCommand arenaFormidlingsgruppeEvent);

    Arbeidssokerperioder finnFormidlingsgrupper(Foedselsnummer foedselsnummer);

    Arbeidssokerperioder finnFormidlingsgrupper(List<Foedselsnummer> foedselsnummerList);
}
