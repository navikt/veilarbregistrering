package no.nav.fo.veilarbregistrering.oppgave;

import no.nav.fo.veilarbregistrering.bruker.AktorId;

public interface OppgaveGateway {

    Oppgave opprettOppgave(AktorId aktoerId, String tilordnetRessurs, String beskrivelse);

    Oppgave opprettOppgaveArbeidstillatelse(AktorId aktoerId, String beskrivelse);

}
