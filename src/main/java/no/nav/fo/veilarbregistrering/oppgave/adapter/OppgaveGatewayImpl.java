package no.nav.fo.veilarbregistrering.oppgave.adapter;

import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway;

import java.time.LocalDate;

public class OppgaveGatewayImpl implements OppgaveGateway {

    private final OppgaveRestClient restClient;

    public OppgaveGatewayImpl(OppgaveRestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public long opprettOppgave(String aktoerId) {
        OppgaveDto oppgaveDto = new OppgaveDto();
        oppgaveDto.setAktoerId(aktoerId);
        oppgaveDto.setBeskrivelse("Bruker får ikke registrert seg som arbeidssøker pga. mulig IARBS i Arena.");
        oppgaveDto.setTema("OPP");
        oppgaveDto.setOppgavetype("KONT_BRUK");
        oppgaveDto.setFristFerdigstillelse(LocalDate.now().plusDays(2).toString()   );
        oppgaveDto.setAktivDato(LocalDate.now().toString());
        oppgaveDto.setPrioritet("LAV");

        OppgaveResponseDto oppgaveResponseDto = restClient.opprettOppgave(oppgaveDto);

        return oppgaveResponseDto.getId();
    }
}
