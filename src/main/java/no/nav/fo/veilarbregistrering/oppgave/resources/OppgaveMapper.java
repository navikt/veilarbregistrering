package no.nav.fo.veilarbregistrering.oppgave.resources;

import no.nav.fo.veilarbregistrering.oppgave.OppgaveResponse;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveType;

class OppgaveMapper {

    private OppgaveMapper() {
    }

    static OppgaveDto map(OppgaveResponse oppgaveResponse, OppgaveType oppgaveType) {
        return new OppgaveDto(oppgaveResponse.getId(), oppgaveResponse.getTildeltEnhetsnr(), oppgaveType);
    }
}
