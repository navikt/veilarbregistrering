package no.nav.fo.veilarbregistrering.oppgave.adapter;

import no.nav.fo.veilarbregistrering.oppgave.Oppgave;
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway;
import no.nav.fo.veilarbregistrering.registrering.bruker.AktorId;

import java.time.LocalDate;

public class OppgaveGatewayImpl implements OppgaveGateway {

    private static final String KONTAKT_BRUKER = "KONT_BRUK";
    private static final String OPPFOLGING = "OPP";
    private static final String LAV = "LAV";

    private final OppgaveRestClient restClient;

    public OppgaveGatewayImpl(OppgaveRestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Oppgave opprettOppgave(AktorId aktoerId, String tilordnetRessurs, String beskrivelse) {
        OppgaveDto oppgaveDto = new OppgaveDto();
        oppgaveDto.setAktoerId(aktoerId.asString());
        oppgaveDto.setBeskrivelse(beskrivelse);
        oppgaveDto.setTilordnetRessurs(tilordnetRessurs);
        oppgaveDto.setTema(OPPFOLGING);
        oppgaveDto.setOppgavetype(KONTAKT_BRUKER);
        oppgaveDto.setFristFerdigstillelse(LocalDate.now().plusDays(2).toString());
        oppgaveDto.setAktivDato(LocalDate.now().toString());
        oppgaveDto.setPrioritet(LAV);

        return restClient.opprettOppgave(oppgaveDto);
    }
}
