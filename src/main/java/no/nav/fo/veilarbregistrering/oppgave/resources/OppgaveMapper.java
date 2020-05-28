package no.nav.fo.veilarbregistrering.oppgave.resources;

import no.nav.fo.veilarbregistrering.oppgave.OppgaveResponse;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveType;

class OppgaveMapper {

    private OppgaveMapper() {
    }

    static OppgaveDto map(OppgaveResponse oppgaveResponse, OppgaveType oppgaveType) {
        OppgaveDto oppgaveDto = new OppgaveDto();
        oppgaveDto.setId(oppgaveResponse.getId());
        oppgaveDto.setTildeltEnhetsnr(oppgaveResponse.getTildeltEnhetsnr());
        oppgaveDto.setOppgaveType(oppgaveType);
        return oppgaveDto;
    }
}
