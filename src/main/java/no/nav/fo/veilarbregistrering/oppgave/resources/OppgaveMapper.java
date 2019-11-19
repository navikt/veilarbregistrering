package no.nav.fo.veilarbregistrering.oppgave.resources;

import no.nav.fo.veilarbregistrering.oppgave.Oppgave;

class OppgaveMapper {

    private OppgaveMapper() {
    }

    static OppgaveDto map(Oppgave oppgave) {
        OppgaveDto oppgaveDto = new OppgaveDto();
        oppgaveDto.setId(oppgave.getId());
        oppgaveDto.setTildeltEnhetsnr(oppgave.getTildeltEnhetsnr());
        return oppgaveDto;
    }
}
