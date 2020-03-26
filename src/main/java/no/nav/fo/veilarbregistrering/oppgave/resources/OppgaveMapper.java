package no.nav.fo.veilarbregistrering.oppgave.resources;

import no.nav.fo.veilarbregistrering.oppgave.Oppgave;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveType;

class OppgaveMapper {

    private OppgaveMapper() {
    }

    static OppgaveDto map(Oppgave oppgave, OppgaveType oppgaveType) {
        OppgaveDto oppgaveDto = new OppgaveDto();
        oppgaveDto.setId(oppgave.getId());
        oppgaveDto.setTildeltEnhetsnr(oppgave.getTildeltEnhetsnr());
        oppgaveDto.setOppgaveType(oppgaveType);
        return oppgaveDto;
    }
}
