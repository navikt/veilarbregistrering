package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.registrering.bruker.AktorId;

public interface OppgaveGateway {

    Oppgave opprettOppgave(AktorId aktoerId, String tilordnetRessurs, String beskrivelse);

}
