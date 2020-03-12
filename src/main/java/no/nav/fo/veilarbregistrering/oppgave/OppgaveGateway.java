package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.AktorId;

public interface OppgaveGateway {

    Oppgave opprettOppgave(AktorId aktoerId, String tilordnetRessurs, String beskrivelse);

    Oppgave opprettOppgaveDagpenger(AktorId aktoerId, String beskrivelse);

}
